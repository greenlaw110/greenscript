package play.modules.greenscript;

import com.greenscriptool.*;
import com.greenscriptool.utils.BufferResource;
import com.greenscriptool.utils.IBufferLocator;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import play.Logger;
import play.Play;
import play.Play.Mode;
import play.PlayPlugin;
import play.cache.Cache;
import play.exceptions.NoRouteFoundException;
import play.exceptions.UnexpectedException;
import play.jobs.Job;
import play.jobs.JobsPlugin;
import play.libs.Time;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.mvc.Scope.Flash;
import play.templates.Template;
import play.utils.Utils;
import play.vfs.VirtualFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Define a Playframework plugin
 *
 * @author greenlaw110@gmail.com
 * @version 1.2, 2010-10-16
 */
public class GreenScriptPlugin extends PlayPlugin {

    public static final String VERSION = "1.2.11b";

    private static String msg_(String msg, Object... args) {
        return String.format("GreenScript-" + VERSION + "> %1$s",
                String.format(msg, args));
    }

    private static void info_(String msg, Object... args) {
        Logger.info(msg_(msg, args));
    }

    private static void trace_(String msg, Object... args) {
        Logger.trace(msg_(msg, args));
    }

    private static void debug_(String msg, Object... args) {
        Logger.info(msg_(msg, args));
    }

    private Minimizer jsM_;
    private Minimizer cssM_;
    private IDependenceManager jsD_;
    private IDependenceManager cssD_;

    private Properties depConf_;
    private Properties minConf_;

    private HashMap<String, Long> configFiles_;

    private boolean eTag_ = false;

    private boolean rythmPresented_ = false;

    private String versionTag = "";

    public static final String RESOURCES_PARAM = "resources";

    private static Properties defProps_; static {
        defProps_ = new Properties();
        // file paths
        defProps_.put("greenscript.dir.root", "/public");
        defProps_.put("greenscript.dir.js", "javascripts");
        defProps_.put("greenscript.dir.css", "stylesheets");
        defProps_.put("greenscript.dir.minimized", "/public/gs");
        // url paths
        defProps_.put("greenscript.url.root", "/public");
        defProps_.put("greenscript.url.js", "/public/javascripts");
        defProps_.put("greenscript.url.css", "/public/stylesheets");
        defProps_.put("greenscript.url.minimized", "/public/gs");
        // operation switches
        defProps_.setProperty("greenscript.minimize", Play.mode == Mode.PROD ? "true" : "false");
        defProps_.setProperty("greenscript.compress", Play.mode == Mode.PROD ? "true" : "false");
        defProps_.setProperty("greenscript.cache", Play.mode == Mode.PROD ? "true" : "false");
        defProps_.setProperty("greenscript.cache.inmemory", "true");
        defProps_.setProperty("greenscript.less.enabled", "false");
        defProps_.setProperty("greenscript.coffee.enabled", "false");
        defProps_.setProperty("greenscript.inline.process", "false");
        defProps_.setProperty("greenscript.js.cache.check", "10s");
        defProps_.setProperty("greenscript.css.cache.check", "10s");
        defProps_.setProperty("greenscript.lessCompile.postMerge", "false");
        defProps_.setProperty("greenscript.resources.param.enabled", "false");
    }

    public GreenScriptPlugin() {
        //depConf_ = new Properties();
        minConf_ = new Properties();
        minConf_.putAll(defProps_);
    }

    @Override
    public void onLoad() {
        try {
            Class.forName("com.greenlaw110.rythm.play.RythmPlugin");
            rythmPresented_ = true;
            debug_("rythm presented");
        } catch (Exception e) {
            // rythm template engine not presented.
            debug_("rythm not presented");
        }
        StaticRouteResolver.loadStaticRoutes();
    }

    @Override
    public void onConfigurationRead() {

        loadDependencies();

        eTag_ = Play.configuration.getProperty("http.useETag", "true").equalsIgnoreCase("true");

        info_("initialized");
    }

    private boolean stopRouteUpdate_ = false;

    private synchronized void updateRoute_() {
        if (inMemoryCache) {
            String url = cacheUrlPath_();
            Router.addRoute(0, "GET",
                    url + "{key}",
                    "greenscript.Service.getInMemoryCache",
                    null,
                    null);
            Logger.debug("route added: %s handled by %s", url + "{key}", "greenscript.Service.getInMemoryCache");
        } else {
            stopRouteUpdate_ = true;
            Router.load(Play.ctxPath);
            stopRouteUpdate_ = false;
        }
    }

    /**
     * Moved initialize from onApplicationStart to onRoutersLoaded because
     * Servlet 2.4 does not allow you to get the context path from the servletcontext...
     */
    @Override
    public synchronized void onRoutesLoaded() {
        if (stopRouteUpdate_) return;
        StaticRouteResolver.processVersionedRoutes();
        InitializeMinimizers();
        updateRoute_();
    }

    @Override
    public void afterApplicationStart() {
        Properties p = Play.configuration;
        for (ResourceType type : ResourceType.values()) {
            final Minimizer m = type == ResourceType.JS ? jsM_ : cssM_;
            String s = fetchProp_(p, String.format("greenscript%s.cache.check", type.getExtension()));
            int i = "never".equalsIgnoreCase(s) ? -1 : Time.parseDuration(s);
            if (-1 != i) {
                Job<Object> j = new Job<Object>() {
                    @Override
                    public void doJob() {
                        m.checkCache();
                    }
                };
                JobsPlugin.executor.scheduleWithFixedDelay(j, i, i, TimeUnit.SECONDS);
            }
        }
    }

    /*
     * provided here to avoid compilation error when Rythm Template Engine is not presented
     */
    private static final Template VOID_RYTHM_TMPL = new Template() {
        @Override
        public void compile() {
        }

        @Override
        protected String internalRender(Map<String, Object> args) {
            return null;
        }
    };

    private final Pattern P = Pattern.compile(".*tags.rythm.greenscript.*");

    @Override
    public Template loadTemplate(VirtualFile file) {
        if (rythmPresented_) return null; // let rythm to handle it
        if (!file.exists()) return null;
        if (P.matcher(file.relativePath()).matches()) {
            return VOID_RYTHM_TMPL;
        }
        return null;
    }

    @Override
    public void onApplicationStop() {
        cleanUp_();
    }

    public String jsDebugString() {
        return ((DependenceManager) jsD_).debugString();
    }

    public String cssDebugString() {
        return ((DependenceManager) cssD_).debugString();
    }

    private static ThreadLocal<IRenderSession> sessJs_ = new ThreadLocal<IRenderSession>();
    private static ThreadLocal<IRenderSession> sessCss_ = new ThreadLocal<IRenderSession>();

    public static IRenderSession session(String type) {
        ResourceType rt = ResourceType.valueOf(type.toUpperCase());
        switch (rt) {
            case JS:
                return jsSession();
            case CSS:
                return cssSession();
        }
        throw new UnexpectedException("unknown resource type: " + rt.name());
    }

    public static IRenderSession jsSession() {
        return sessJs_.get();
    }

    public static IRenderSession cssSession() {
        return sessCss_.get();
    }

    private static class ResourceResolver extends Controller {
        public static String def(ResourceType type) {
            String template = Controller.template();
            String urlPath = resourceUrl_.get(type.getExtension());
            return null == template ? null : template.replaceFirst("^views/", urlPath).replaceFirst("\\.[\\w]+$", type.getExtension());
        }
    }

    @Override
    public void beforeInvocation() {
        IRenderSession sess = newSession_(ResourceType.JS);
        sessJs_.set(sess);

        sess = newSession_(ResourceType.CSS);
        sessCss_.set(sess);
    }

    @Override
    public void beforeActionInvocation(Method actionMethod) {
        /*
         * Automatically declare js resource
         * e.g /public/javascripts/Application/index.js
         */
        IRenderSession sess = sessJs_.get();
        String def = ResourceResolver.def(ResourceType.JS);
        sess.declare(def, null, null);

        /*
         * Automatically declare js resource
         * e.g. /public/stylesheets/Application/index.css
         */
        sess = sessCss_.get();
        def = ResourceResolver.def(ResourceType.CSS);
        sess.declare(def, null, null);

    }

    //private static YUICompressor jsC_ = new YUICompressor(ResourceType.JS);
    //private static YUICompressor cssC_ = new YUICompressor(ResourceType.CSS);
    @Override
    public boolean serveStatic(VirtualFile file, Request request, Response response) {
        if (null == jsM_) {
            if (Play.mode == Mode.DEV) Play.start();
            else throw new UnexpectedException("Minimizer not initialized");
        }
        String fn = file.getName();
        if (fn.endsWith(".coffee") || (fn.endsWith(".js") && jsM_.isMinimizeEnabled() && !file.relativePath().startsWith(cacheUrlPath_()))) {
            return processStatic_(file, request, response, ResourceType.JS);
        }
        if ((fn.endsWith("css") || fn.endsWith("less")) && cssM_.isMinimizeEnabled() && !file.relativePath().startsWith(cacheUrlPath_())) {
            return processStatic_(file, request, response, ResourceType.CSS);
        }

        if (fn.endsWith(".css") || fn.endsWith(".js")) {
            // minimized resource
            final long l = file.lastModified();
            final String etag = "\"" + l + "-" + file.hashCode() + "\"";
            Map<String, Http.Header> headers = request.headers;
            if (fn.endsWith(".js")) {
                response.setContentTypeIfNotSet("text/javascript");
            } else if (fn.endsWith(".css")) {
                response.setContentTypeIfNotSet("text/css");
            }
            if (headers.containsKey("if-none-match") && headers.containsKey("if-modified-since")) {
                if ("GET".equalsIgnoreCase(request.method)) {
                    response.status = Http.StatusCode.NOT_MODIFIED;
                    response.cacheFor(etag, "100d", l);
                    keepFlash_();
                    return true;
                }
            }
        }

        return false;
    }

//    private static final Pattern P_IMPORT = Pattern.compile(".*@import\\s*\"(.*?)\".*");
//    private Set<File> imports_(File file) {
//        String key = "less_imports_" + file.getPath() + file.lastModified();
//
//        @SuppressWarnings("unchecked")
//        Set<File> files = Cache.get(key, Set.class);
//        if (null == files) {
//            files = new HashSet<File>();
//            try {
//                List<String> lines = IO.readLines(file);
//                for (String line: lines) {
//                    Matcher m = P_IMPORT.matcher(line);
//                    while (m.find()) {
//                        File f = new File(file.getParentFile(), m.group(1));
//                        files.add(f);
//                        files.addAll(imports_(f));
//                    }
//                }
//            } catch (Exception e) {
//                Logger.error(e, "Error occurred getting @imports from resource: $s", file);
//            }
//        }
//        return files;
//    }

//    private long lastModified_(VirtualFile file, ResourceType type) {
//        long l = file.lastModified();
//        if (ResourceType.CSS == type) {
//            // try to get last modified of all @imported files
//            for (File f: imports_(file.getRealFile())) {
//                l = Math.max(l, f.lastModified());
//            }
//        }
//        return l;
//    }

    private void keepFlash_() {
        Flash f = Flash.current();
        if (f != null) f.keep();
    }

    private boolean processStatic_(VirtualFile file, Request req, Response resp, ResourceType type) {
        /*
        IRenderSession sess = type == ResourceType.JS ? jsSession() : cssSession();
        if (null != sess && sess.hasDeclared()) {
            // do not service static if requesting to minimized files
            return false;
        }
        */
        if (Play.mode == Mode.PROD) {
            resp.cacheFor("1h");
        }
        if (type == ResourceType.CSS) {
            resp.setContentTypeIfNotSet("text/css");
        } else if (type == ResourceType.JS) {
            resp.setContentTypeIfNotSet("text/javascript");
        }
        IMinimizer min = type == ResourceType.CSS ? cssM_ : jsM_;
        
        long l = min.getLastModified(file.getRealFile());
        final String etag = "\"" + l + "-" + file.hashCode() + "\"";
        if (!req.isModified(etag, l)) {
            if (req.method.equalsIgnoreCase("GET")) {
                resp.status = Http.StatusCode.NOT_MODIFIED;
                if (eTag_) {
                    resp.setHeader(Names.ETAG, etag);
                }
                keepFlash_();
                return true;
            } else {
                return false;
            }
        } else {
            try {
                String content = min.processStatic(file.getRealFile());
                resp.status = 200;
                resp.print(content);
                resp.setHeader(Names.LAST_MODIFIED, Utils.getHttpDateFormatter().format(new Date(l + 1000)));
                if (eTag_) {
                    resp.setHeader(Names.ETAG, etag);
                }
                keepFlash_();
                return true;
            } catch (Exception e) {
                Logger.error(e, "error compress file %1$s", file.getName());
                return false;
            }
        }
    }

    private HashMap<String, Long> currentConfigFiles() {
        HashMap<String, Long> files = new HashMap<String, Long>();

        for (VirtualFile vf : Play.roots) {
            VirtualFile conf = vf.child("conf/greenscript.conf");
            if (conf.exists()) {
                files.put(conf.getRealFile().getAbsolutePath(), conf.getRealFile().lastModified());
            }
        }

        return files;
    }

    private boolean filesChanged(HashMap<String, Long> oldFiles, HashMap<String, Long> newFiles) {
        if (oldFiles.size() != newFiles.size()) {
            return true;
        }

        for (Entry<String, Long> entry : oldFiles.entrySet()) {
            Long newTime = newFiles.get(entry.getKey());
            if (newTime == null || !newTime.equals(entry.getValue())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void detectChange() {
        if (Play.mode == Play.Mode.PROD) {
            return;
        }

        if (filesChanged(this.configFiles_, currentConfigFiles())) {
            Logger.debug("greenscript: config files changed, reloading dependencies");
            GreenScriptPlugin.reloadDependencies();
        }
    }

    public void loadDependencies() {
        Properties p = new Properties();
        for (VirtualFile vf : Play.roots) {
            VirtualFile conf = vf.child("conf/greenscript.conf");
            if (conf.exists()) {
                //info_("loading dependency configuration from %1$s", conf.getRealFile().getAbsolutePath());
                try {
                    Properties p0 = new Properties();
                    p0.load(new BufferedInputStream(conf.inputstream()));
                    for (String k : p0.stringPropertyNames()) {
                        //info_("loading property: %s", k);
                        if (!p.containsKey(k)) {
                            info_("loading property for %s: %s", k, p0.get(k));
                            p.put(k, p0.get(k));
                        } else {
                            String v = p.getProperty(k);
                            String v0 = p0.getProperty(k);
                            v = v + "," + v0;
                            //info_("loading duplicate property for %s: %s", k, v);
                            p.setProperty(k, v);
                        }
                    }
                } catch (Exception e) {
                    throw new UnexpectedException("error loading conf/greenscript.conf");
                }
            }
        }
        this.configFiles_ = this.currentConfigFiles();
        //info_("greenscript.conf loaded: %s", p);
        jsD_ = new DependenceManager(loadDepProp_(p, "js"));
        cssD_ = new DependenceManager(loadDepProp_(p, "css"));

        depConf_ = p;
        info_("dependency loaded");
    }

    public void InitializeMinimizers() {
        Properties p = Play.configuration;

        for (String key : p.stringPropertyNames()) {
            if (key.startsWith("greenscript.")) {
                String v = p.getProperty(key);
                minConf_.setProperty(key, p.getProperty(key));
                trace_("[greenscript]set %1$s to %2$s", v, key);
            }
        }

        jsM_ = initializeMinimizer_(minConf_, ResourceType.JS);
        cssM_ = initializeMinimizer_(minConf_, ResourceType.CSS);


        if (p.containsKey("greenscript.coffee.enabled")) {
            System.setProperty("greenscript.coffee.enabled", p.getProperty("greenscript.coffee.enabled"));
        }

        if (p.containsKey("greenscript.less.enabled")) {
            System.setProperty("greenscript.less.enabled", p.getProperty("greenscript.less.enabled"));
        }

        info_("minimizer initialized");
    }

    private IRenderSession newSession_(ResourceType type) {
        return type == ResourceType.JS ?
                new RenderSession(jsM_, jsD_, type) :
                new RenderSession(cssM_, cssD_, type);
    }

    public Properties getDependencyConfig() {
        Properties p = new Properties();
        p.putAll(depConf_);
        return p;
    }

    public Properties getMinimizerConfig() {
        Properties p = new Properties();
        p.putAll(minConf_);
        return p;
    }

    private String join_(Collection<String> c) {
        boolean first = true;
        StringBuffer sb = new StringBuffer();
        for (String s : c) {
            if (!first) sb.append(",");
            else first = false;
            sb.append(s);
        }
        return sb.toString();
    }

    private void mergeProperties_(Properties p, String k, String v) {
        String oldV = p.getProperty(k);
        if (null == oldV) {
            p.setProperty(k, v);
        } else {
            Set<String> oldS = new HashSet<String>();
            oldS.addAll(Arrays.asList(oldV.split(IDependenceManager.SEPARATOR)));
            Set<String> newS = new HashSet<String>();
            newS.addAll(Arrays.asList(v.split(IDependenceManager.SEPARATOR)));
            oldS.addAll(newS);
            p.setProperty(k, join_(oldS));
        }
    }

    // type should be "js" or "css"
    private Properties loadDepProp_(Properties p, String type) {
        Properties p0 = new Properties();
        String prefix = type + ".";
        for (String k : p.stringPropertyNames()) {
            if (k.startsWith(prefix)) {
                String k0 = k.replace(prefix, "");
                String v = p.getProperty(k);
                if (k0.matches(".*\\s*\\-\\s*$")) {
                    // reverse dependency declaration
                    k0 = k0.replaceAll("\\s*\\-\\s*$", "");
                    for (String s : v.replaceAll("\\s+", "").split(IDependenceManager.SEPARATOR)) {
                        mergeProperties_(p0, s, k0);
                    }
                } else {
                    mergeProperties_(p0, k0, v);
                }
                trace_("Found one %1$s dependency: %2$s depends on '%3$s'", type, k0, v);
            }
        }
        return p0;
    }

    public static final String CACHE_KEY_BUFFER = "greenscript.buffer";
    protected boolean inMemoryCache = false;
    private static Map<String, String> resourceUrl_ = new HashMap<String, String>();

    private void setResourceUrlPath_(String resourceUrlRoot, String resourceUrlPath, String ext) {
        String path = null, ctxPath = Play.ctxPath;
        if (!resourceUrlPath.endsWith("/"))
            resourceUrlPath = resourceUrlPath + "/";
        if (resourceUrlPath.startsWith("/")) {
            path = resourceUrlPath.startsWith(ctxPath) ? resourceUrlPath : ctxPath + resourceUrlPath;
        } else {
            path = resourceUrlRoot + resourceUrlPath;
        }
        resourceUrl_.put(ext, path);
    }

    private String resourceUrlRoot_() {
        Properties p = minConf_;
        String urlRoot = fetchProp_(p, "greenscript.url.root");
        if (!urlRoot.startsWith("/"))
            throw new IllegalArgumentException("url root must start with /");
        // checkInitialize_(false);
        if (!urlRoot.endsWith("/"))
            urlRoot = urlRoot + "/";

        return urlRoot.startsWith(Play.ctxPath) ? urlRoot : Play.ctxPath + urlRoot;
    }

    private String cacheUrlPath_() {
        String resourceUrlRoot = resourceUrlRoot_();
        if (null == resourceUrlRoot) {
            throw new IllegalStateException("resourceUrlRoot must be initiated first");
        }
        Properties p = Play.configuration;
        String urlPath = fetchProp_(p, "greenscript.url.minimized"), ctxPath = Play.ctxPath;
        if (!urlPath.endsWith("/"))
            urlPath = urlPath + "/";
        // add version info
        VirtualFile vf = Play.getVirtualFile(".version");
        if (null != vf) {
            String v = vf.contentAsString();
            urlPath = urlPath + v + "/";
        }
        if (urlPath.startsWith("/")) {
            return urlPath.startsWith(ctxPath) ? urlPath : ctxPath + urlPath;
        } else {
            return resourceUrlRoot + urlPath;
        }
    }

    private Minimizer initializeMinimizer_(Properties p, ResourceType type) {
        final Minimizer m = new Minimizer(type);
        m.setFileLocator(new IFileLocator() {
            @Override
            public File locate(String path) {
                VirtualFile vf = VirtualFile.search(Play.roots, path);
                return vf == null ? null : vf.getRealFile();
            }
        });
        m.setBufferLocator(bufferLocator_);

        boolean routerMapping = getBooleanProp_(p, "greenscript.router.mapping", false);

        if (routerMapping) {
            m.setRouteMapper(new IRouteMapper() {
                @Override
                public String reverse(String fileName) {
                    try {
                        String url = Router.reverseWithCheck(fileName, Play.getVirtualFile(fileName), false);
                        if (fileName.endsWith("/") && !url.endsWith("/")) {
                            url = url + "/";
                        }
                        return url;
                    } catch (NoRouteFoundException e) {
                        return fileName;
                    }
                }

                @Override
                public String route(String url) {
                    return StaticRouteResolver.route(url);
//                    try {
//                        Map<String, String> args = Router.route("GET", url);
//                        return args.get("action");
//                    } catch (RenderStatic rs) {
//                        String fileName = rs.file;
//                        if (url.startsWith("/") && !fileName.startsWith("/")) {
//                            fileName = "/" + fileName;
//                        }
//                        return fileName;
//                    } catch (NotFound ex) {
//                        return url;
//                    }
                }
            });
        }

        String ext = type.getExtension();
        String rootDir = fetchProp_(p, "greenscript.dir.root");
        String resourceDir = fetchProp_(p, "greenscript.dir" + ext);
        String cacheDir = fetchProp_(p, "greenscript.dir.minimized");

        String urlRoot = resourceUrlRoot_();
        String resourceUrl = fetchProp_(p, "greenscript.url" + ext);
        String cacheUrl = cacheUrlPath_();
        setResourceUrlPath_(urlRoot, resourceUrl, ext);

        m.setUrlContextPath(Play.ctxPath);
        m.setResourceUrlRoot(urlRoot);
        resourceUrl = StaticRouteResolver.addVersion(resourceUrl);
        m.setResourceUrlPath(resourceUrl);
        m.setCacheUrlPath(cacheUrl);
        m.setRootDir(rootDir);
        m.setCacheDir(Play.getFile(cacheDir));
        m.setResourceDir(resourceDir);

        boolean resourcesParameter = getBooleanProp_(p, "greenscript.resources.param.enabled", false);
        m.setResourcesParam(resourcesParameter ? RESOURCES_PARAM : null);

        boolean minimize = getBooleanProp_(p, "greenscript.minimize", Play.mode == Mode.PROD);
        boolean compress = getBooleanProp_(p, "greenscript.compress", true);
        boolean cache = getBooleanProp_(p, "greenscript.cache", true);
        inMemoryCache = getBooleanProp_(p, "greenscript.cache.inmemory", false);
        boolean processInline = getBooleanProp_(p, "greenscript.inline.process", false);
        System.setProperty("greenscript.lessCompile.postMerge", fetchProp_(p, "greenscript.lessCompile.postMerge"));

        m.enableDisableMinimize(minimize);
        m.enableDisableCompress(compress);
        m.enableDisableCache(cache);
        m.enableDisableInMemoryCache(inMemoryCache);
        m.enableDisableProcessInline(processInline);

        trace_("minimizer for %1$s loaded", type.name());
        return m;
    }

    public String getInMemoryFileContent(String key, String resourceNames) {
        IResource resource = bufferLocator_.locate(key);

        if (resource == null && resourceNames != null) {
            Minimizer minimizer = null;
            // Select the minimizer.
            if (key.endsWith(".js")) {
                minimizer = jsM_;
            } else if (key.endsWith(".css")) {
                minimizer = cssM_;
            }

            resource = minimizer.minimize(resourceNames);
        }

        return null == resource ? null : resource.toString();
    }

    private IBufferLocator bufferLocator_ = new IBufferLocator() {
        private String key_(String key) {
            return String.format("%s.%s", CACHE_KEY_BUFFER, key);
        }

        @Override
        public BufferResource locate(String key) {
            return Cache.get(key_(key), BufferResource.class);
        }

        @Override
        public BufferResource newBuffer(List<String> resourceNames, String extension) {
            StringBuilder builder = new StringBuilder();
            for (String resourceName : resourceNames) {
                builder.append(resourceName);
            }

            String key = UUID.nameUUIDFromBytes(builder.toString().getBytes()).toString() + extension;

            Logger.trace("Created key '%s' from resources '%s' and extension '%s'", key, builder.toString(), extension);

            BufferResource buffer = new BufferResource(key);
            Cache.set(key_(key), buffer);
            return buffer;
        }
    };

    private static String fetchProp_(Properties p, String key) {
        String val = p.getProperty(key);
        if (null == val) val = defProps_.getProperty(key);
        return val;
    }

    private boolean getBooleanProp_(Properties p, String key, boolean def) {
        try {
            String s = p.containsKey(key) ? p.getProperty(key)
                    : defProps_.containsKey(key) ? defProps_.getProperty(key)
                    : String.valueOf(def);

            p.setProperty(key, s);
            return Boolean.parseBoolean(s);
        } catch (Exception e) {
            p.setProperty(key, String.valueOf(def));
            return def;
        }
    }

//    private File getDir_(String dir) {
//        return Play.getFile(dir);
//    }

    private void cleanUp_() {
        if (null != jsM_) jsM_.clearCache();
        if (null != cssM_) cssM_.clearCache();
    }

    public static GreenScriptPlugin getInstance() {
        for (PlayPlugin pp : Play.pluginCollection.getEnabledPlugins()) {
            if (pp instanceof GreenScriptPlugin) return (GreenScriptPlugin) pp;
        }
        return null;
    }

    public static void updateMinimizer(boolean minimize, boolean compress, boolean cache, boolean inMemoryCache) {
        GreenScriptPlugin gs = getInstance();
        gs.jsM_.enableDisableMinimize(minimize);
        gs.jsM_.enableDisableCompress(compress);
        gs.jsM_.enableDisableCache(cache);
        gs.jsM_.enableDisableInMemoryCache(inMemoryCache);

        gs.cssM_.enableDisableMinimize(minimize);
        gs.cssM_.enableDisableCompress(compress);
        gs.cssM_.enableDisableCache(cache);
        gs.cssM_.enableDisableInMemoryCache(inMemoryCache);
        gs.inMemoryCache = inMemoryCache;

        gs.updateRoute_();

        gs.minConf_.setProperty("greenscript.minimize", String.valueOf(minimize));
        gs.minConf_.setProperty("greenscript.compress", String.valueOf(compress));
        gs.minConf_.setProperty("greenscript.cache", String.valueOf(cache));
        gs.minConf_.setProperty("greenscript.cache.inmemory", String.valueOf(inMemoryCache));
    }

    public static void enableDisableMinimize(boolean minimize) {
        GreenScriptPlugin gs = getInstance();
        gs.jsM_.enableDisableMinimize(minimize);
        gs.minConf_.setProperty("greenscript.minimize", String.valueOf(minimize));
        info_("minimize %s", minimize ? "enabled" : "disabled");
    }

    public static void enableDisableCompress(boolean compress) {
        GreenScriptPlugin gs = getInstance();
        gs.jsM_.enableDisableCompress(compress);
        gs.minConf_.setProperty("greenscript.compress", String.valueOf(compress));
        info_("compress %s", compress ? "enabled" : "disabled");
    }

    public static void enableDisableCache(boolean cache) {
        GreenScriptPlugin gs = getInstance();
        gs.jsM_.enableDisableCache(cache);
        gs.minConf_.setProperty("greenscript.cache", String.valueOf(cache));
        info_("cache %s", cache ? "enabled" : "disabled");
    }

    public static void reloadDependencies() {
        GreenScriptPlugin gs = getInstance();
        gs.loadDependencies();
        info_("dependency reloaded");
    }

    public static String lessImport(String fns) {
        GreenScriptPlugin gs = getInstance();
        Properties p = gs.minConf_;
        String rootDir = fetchProp_(p, "greenscript.dir.root");
        String resourceDir = fetchProp_(p, "greenscript.dir.css");
        StringBuilder sb = new StringBuilder();
        String[] sa = fns.split("[ ,;]");
        for (String fn : sa) {
            fn = fn.endsWith(".css") ? fn : fn + ".css";
            String path;
            if (fn.startsWith("/")) {
                path = (!fn.startsWith(rootDir)) ? rootDir
                        + fn.replaceFirst("/", "") : fn;
            } else {
                path = rootDir + File.separator + resourceDir + File.separator
                        + fn;
            }
            VirtualFile vf = VirtualFile.search(Play.roots, path);
            if (null != vf) {
                sb.append(vf.contentAsString());
            }
        }
        return sb.toString();
    }
}
