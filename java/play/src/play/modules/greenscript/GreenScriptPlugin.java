package play.modules.greenscript;

import java.io.BufferedInputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import org.jboss.netty.handler.codec.http.HttpHeaders.Names;

import play.Logger;
import play.Play;
import play.Play.Mode;
import play.PlayPlugin;
import play.cache.Cache;
import play.exceptions.UnexpectedException;
import play.jobs.Job;
import play.jobs.JobsPlugin;
import play.libs.Time;
import play.mvc.Http;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.mvc.Router;
import play.utils.Utils;
import play.vfs.VirtualFile;

import com.greenscriptool.DependenceManager;
import com.greenscriptool.IDependenceManager;
import com.greenscriptool.IFileLocator;
import com.greenscriptool.IMinimizer;
import com.greenscriptool.IRenderSession;
import com.greenscriptool.Minimizer;
import com.greenscriptool.RenderSession;
import com.greenscriptool.ResourceType;
import com.greenscriptool.utils.BufferResource;
import com.greenscriptool.utils.IBufferLocator;

/**
 * Define a Playframework plugin
 * 
 * @author greenlaw110@gmail.com
 * @version 1.2.6, 2011-09-04
 *          support LESS, fix bug: https://github.com/greenlaw110/greenscript/issues/18
 * @version 1.2.5, 2011-08-07
 *          support in-memory cache
 * @version 1.2.1, 2011-01-20 
 *           1. support reverse dependency declaration, e.g:
 *           * js.jquery-1.4.4-=jquery-ui.1.8.7,jquery.tmpl
 * @version 1.2, 2010-10-16
 */
public class GreenScriptPlugin extends PlayPlugin {

    public static final String VERSION = "1.2.6d";

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

    private Minimizer jsM_;
    private Minimizer cssM_;
    private IDependenceManager jsD_;
    private IDependenceManager cssD_;
    
    private Properties depConf_;
    private Properties minConf_;
    
    private HashMap<String, Long> configFiles_;
    
    private boolean eTag_ = false;
    
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
        defProps_.setProperty("greenscript.compress", "true");
        defProps_.setProperty("greenscript.cache", "true");
        defProps_.setProperty("greenscript.cache.inmemory", "false");
        defProps_.setProperty("greenscript.less.enabled", "false");
        defProps_.setProperty("greenscript.inline.process", "false");
        defProps_.setProperty("greenscript.js.cache.check", "10s");
        defProps_.setProperty("greenscript.css.cache.check", "10s");
    }
    
    public GreenScriptPlugin() {
        //depConf_ = new Properties();
        minConf_ = new Properties();
        minConf_.putAll(defProps_);
    }
    
    @Override
    public void onConfigurationRead() {
        
        loadDependencies();
        InitializeMinimizers();
        
        eTag_ = Play.configuration.getProperty("http.useETag", "true").equalsIgnoreCase("true");
        
        info_("initialized");
    }
    
    private void updateRoute_() {
        if (inMemoryCache) {
            String url = fetchProp_(Play.configuration, "greenscript.url.minimized");
            Router.addRoute(0, "GET", 
                    url + "/{key}", 
                    "greenscript.Service.getInMemoryCache",
                    null,
                    null);
        } else {
            Router.load(Play.ctxPath);
        }
    }
    
    @Override
    public void onApplicationStart() {
        updateRoute_();
    }
    
    @Override
    public void afterApplicationStart() {
        Properties p = Play.configuration;
        for (ResourceType type: ResourceType.values()) {
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
    
    @Override
    public void onApplicationStop() {
        cleanUp_();
    }
    
    public String jsDebugString() {
        return ((DependenceManager)jsD_).debugString();
    }
    
    public String cssDebugString() {
        return ((DependenceManager)cssD_).debugString();
    }
    
    private static ThreadLocal<IRenderSession> sessJs_ = new ThreadLocal<IRenderSession>();
    private static ThreadLocal<IRenderSession> sessCss_ = new ThreadLocal<IRenderSession>();
    
    public static IRenderSession session(String type) {
        ResourceType rt = ResourceType.valueOf(type.toUpperCase());
        switch (rt) {
        case JS: return jsSession();
        case CSS: return cssSession();
        }
        throw new UnexpectedException("unknown resource type: " + rt.name());
    }
    
    public static IRenderSession jsSession() {
        return sessJs_.get();
    }
    
    public static IRenderSession cssSession() {
        return sessCss_.get();
    }

    @Override
    public void beforeActionInvocation(Method actionMethod) {
        sessJs_.set(newSession_(ResourceType.JS));
        sessCss_.set(newSession_(ResourceType.CSS));
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
        if (jsM_.isMinimizeEnabled() && fn.endsWith(".js")) {
            return processStatic_(file, request, response, ResourceType.JS);
        }
        if (cssM_.isMinimizeEnabled() && (fn.endsWith("css") || fn.endsWith("less"))) {
            return processStatic_(file, request, response, ResourceType.CSS);
        }
        
        return super.serveStatic(file, request, response);
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
    
    private boolean processStatic_(VirtualFile file, Request req, Response resp, ResourceType type) {
        IRenderSession sess = type == ResourceType.JS ? jsSession() : cssSession();
        if (null != sess && sess.hasDeclared()) {
            // do not service static if requesting to minimized files
            return false;
        }
        if (Play.mode == Mode.PROD) {
            resp.cacheFor("1h");
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
                return true;
            } else {
                return false;
            }
        } else {
            
            try {
                String content = min.processStatic(file.getRealFile());
                resp.contentType = type == ResourceType.JS ? "text/javascript" : "text/css";
                resp.status = 200;
                resp.print(content);
                resp.setHeader(Names.LAST_MODIFIED, Utils.getHttpDateFormatter().format(new Date(l + 1000)));
                if (eTag_) {
                    resp.setHeader(Names.ETAG, etag);
                }
                return true;
            } catch (Exception e) {
                Logger.error(e, "error compress file %1$s", file.getName());
                return false;
            }
        }
    }

    private HashMap<String, Long> currentConfigFiles() {
        HashMap<String, Long> files = new HashMap<String, Long>();
        
        for(VirtualFile vf : Play.roots) {
            VirtualFile conf = vf.child("conf/greenscript.conf");
            if (conf.exists()) {
                files.put(conf.getRealFile().getAbsolutePath(), conf.getRealFile().lastModified());
            }
        }
        
        return files;
    }
    
    private boolean filesChanged(HashMap<String, Long> oldFiles,  HashMap<String, Long> newFiles) {
        if(oldFiles.size() != newFiles.size()) {
            return true;
        }
        
        for(Entry<String, Long> entry : oldFiles.entrySet()) {
            Long newTime = newFiles.get(entry.getKey());
            if(newTime == null || ! newTime.equals(entry.getValue())) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public void detectChange() {
        if(Play.mode == Play.Mode.PROD) {
            return;
        }
        
        if(filesChanged(this.configFiles_, currentConfigFiles())) {
            Logger.debug("greenscript: config files changed, reloading dependencies");
            GreenScriptPlugin.reloadDependencies();
        }
    }
    
    public void loadDependencies() {
        Properties p = new Properties();
        for (VirtualFile vf: Play.roots) {
            VirtualFile conf = vf.child("conf/greenscript.conf");
            if (conf.exists()) {
                trace_("loading dependency configuration from %1$s", conf.getRealFile().getAbsolutePath());
                try {
                    p.load(new BufferedInputStream(conf.inputstream()));
                } catch (Exception e) {
                    throw new UnexpectedException("error loading conf/greenscript.conf");
                }
            }
        }
        this.configFiles_ = this.currentConfigFiles();
        jsD_ = new DependenceManager(loadDepProp_(p, "js"));
        cssD_ = new DependenceManager(loadDepProp_(p, "css"));
        
        depConf_ = p;
        info_("dependency loaded");
    }
    
    public void InitializeMinimizers() {
        Properties p = Play.configuration;
        
        for (String key: p.stringPropertyNames()) {
            if (key.startsWith("greenscript.")) {
                String v = p.getProperty(key);
                minConf_.setProperty(key, p.getProperty(key));
                trace_("[greenscript]set %1$s to %2$s", v, key);
            }
        }

        jsM_ = initializeMinimizer_(minConf_, ResourceType.JS);
        cssM_ = initializeMinimizer_(minConf_, ResourceType.CSS);

        
        if (p.containsKey("greenscript.useGoogleClosure")) {
            System.setProperty("greenscript.useGoogleClosure", p.getProperty("greenscript.useGoogleClosure"));
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
        for (String s: c) {
            if (!first) sb.append(",");
            else first = false;
            sb.append(s);
        }
        return sb.toString();
    }
    
    private void mergeProperties_(Properties p, String k, String v){
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
        for (String k: p.stringPropertyNames()) {
            if (k.startsWith(prefix)) {
                String k0 = k.replace(prefix, "");
                String v = p.getProperty(k);
                if (k0.matches(".*\\s*\\-\\s*$")) {
                    // reverse dependency declaration
                    k0 = k0.replaceAll("\\s*\\-\\s*$", "");
                    for (String s: v.replaceAll("\\s+", "").split(IDependenceManager.SEPARATOR)) {
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
    private Minimizer initializeMinimizer_(Properties p, ResourceType type) {
        final Minimizer m = new Minimizer(type);
        String ext = type.getExtension();
        String rootDir = fetchProp_(p, "greenscript.dir.root");
        String resourceDir = fetchProp_(p, "greenscript.dir" + ext);
        String cacheDir = fetchProp_(p, "greenscript.dir.minimized");
        
        String urlRoot = fetchProp_(p, "greenscript.url.root");
        String resourceUrl = fetchProp_(p, "greenscript.url" + ext);
        String cacheUrl = fetchProp_(p, "greenscript.url.minimized");
        
        m.setCacheDir(Play.getFile(cacheDir));
        m.setCacheUrlPath(cacheUrl);
        m.setResourceDir(resourceDir);
        m.setResourceUrlRoot(urlRoot);
        m.setResourceUrlPath(resourceUrl);
        m.setRootDir(rootDir);
        
        boolean minimize = getBooleanProp_(p, "greenscript.minimize", Play.mode == Mode.PROD);
        boolean compress = getBooleanProp_(p, "greenscript.compress", true);
        boolean cache = getBooleanProp_(p, "greenscript.cache", true);
        inMemoryCache = getBooleanProp_(p, "greenscript.cache.inmemory", false);
        boolean processInline = getBooleanProp_(p, "greenscript.inline.process", false);
        
        m.enableDisableMinimize(minimize);
        m.enableDisableCompress(compress);
        m.enableDisableCache(cache);
        m.enableDisableInMemoryCache(inMemoryCache);
        m.enableDisableProcessInline(processInline);
        m.setFileLocator(new IFileLocator(){
            @Override
            public File locate(String path) {
                VirtualFile vf = VirtualFile.search(Play.roots, path);
                return vf == null ? null : vf.getRealFile();
            }
        });
        m.setBufferLocator(bufferLocator_);
        
        trace_("minimizer for %1$s loaded", type.name());
        return m;
    }
    
    public String getInMemoryFileContent(String key) {
        BufferResource br = bufferLocator_.locate(key);
        return null == br ? null : br.toString();
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
        public BufferResource newBuffer(String extension) {
            String key = UUID.randomUUID().toString() + extension;
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
        for (PlayPlugin pp: Play.pluginCollection.getEnabledPlugins()) {
            if (pp instanceof GreenScriptPlugin) return (GreenScriptPlugin)pp;
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
        for (String fn: sa) {
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
/*
 * History
 * -----------------------------------------------------------
 * 1.2.6
 *  - support LESS
 * 1.2.5:
 *  - support in memory cache
 * 1.2.3:
 *  - upgrade YUI compressor from 2.4.2 to 2.4.6
 *  - Fix bug: 404 error while fetching cached files when change minimize/cache setting dynamically
 *  - Fix bug: loaded logic breaks when minimize is enabled
 * 1.2.2: 
 *  - use Play.pluginCollection.getEnabledPlugins() in place of Play.plugins
 *  - greenscript.conf hot reloads on changes in DEV mode, merge from shorty-at / greenscript
 *  - add bundle and cdn test application
 */