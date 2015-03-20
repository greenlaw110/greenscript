package com.greenscriptool;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;
import com.greenscriptool.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jcoffeescript.JCoffeeScriptCompileException;
import org.jcoffeescript.JCoffeeScriptCompiler;

import javax.inject.Inject;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Minimizer implements IMinimizer {

    private static Log logger_ = LogFactory.getLog(Minimizer.class);

    private boolean minimize_;
    private boolean compress_;
    private boolean useCache_;
    private boolean inMemory_;
    private boolean processInline_;

    private FileCache cache_ = null;
    private String resourcePath_ = null;
    private String rootDir_ = null;

    private String ctxPath_ = null;
    private String resourceUrlRoot_ = null;
    private String resourceUrlPath_ = null;
    private String cacheUrlPath_ = null;
    private String resourcesParam_ = null;

    private ICompressor compressor_;
    private ResourceType type_;

    private LessEngine less_;
    private JCoffeeScriptCompiler coffee_;

    private void init_(ICompressor compressor, ResourceType type) {
        if (null == compressor)
            throw new NullPointerException();
        compressor_ = compressor;
        type_ = type;
        less_ = new LessEngine();
        coffee_ = new JCoffeeScriptCompiler();
    }

    public Minimizer(ResourceType type) {
        ICompressor compressor = type == ResourceType.CSS ? new YUICompressor(type) : new SimpleJSCompressor(type);
        init_(compressor, type);
    }

    @Inject
    public Minimizer(ICompressor compressor, ResourceType type) {
        init_(compressor, type);
    }

    @Override
    public void enableDisableMinimize(boolean enable) {
        minimize_ = enable || ResourceType.CSS == type_;
        if (logger_.isDebugEnabled())
            logger_.debug("minimize " + (enable ? "enabled" : "disabled"));
        clearCache();
    }

    @Override
    public void enableDisableCompress(boolean enable) {
        compress_ = enable;
        if (logger_.isDebugEnabled())
            logger_.debug("compress " + (enable ? "enabled" : "disabled"));
        clearCache();
    }

    @Override
    public void enableDisableCache(boolean enable) {
        useCache_ = enable;
        if (logger_.isDebugEnabled())
            logger_.debug("cache " + (enable ? "enabled" : "disabled"));
        clearCache();
    }

    @Override
    public void enableDisableInMemoryCache(boolean enable) {
        inMemory_ = enable;
        if (logger_.isDebugEnabled())
            logger_.debug("in memory cache "
                    + (enable ? "enabled" : "disabled"));
        clearCache();
    }

    @Override
    public void enableDisableProcessInline(boolean enable) {
        processInline_ = enable;
        if (logger_.isDebugEnabled())
            logger_.debug("inline processing "
                    + (enable ? "enabled" : "disabled"));
    }

    @Deprecated
    public void enableDisableVerifyResource(boolean verify) {
        // verifyResource_ = verify;
    }

    @Override
    public boolean isMinimizeEnabled() {
        // now css type resource is always minimized
        return minimize_ || ResourceType.CSS == type_;
    }

    @Override
    public boolean isCompressEnabled() {
        return compress_;
    }

    @Override
    public boolean isCacheEnabled() {
        return useCache_;
    }

    @Override
    public void setResourceDir(String dir) {
        checkInitialize_(false);
        if (rootDir_ == null) throw new IllegalStateException("rootDir need to be intialized first");
        if (dir.startsWith(rootDir_)) {
            resourcePath_ = dir;
        } else if (dir.startsWith("/")) {
            resourcePath_ = rootDir_ + dir;
        } else {
            resourcePath_ = rootDir_ + "/" + dir;
        }
        File f = fl_.locate(resourcePath_);
        if (!f.isDirectory()) throw new IllegalArgumentException("not a directory");
    }

    @Override
    public void setRootDir(String dir) {
        checkInitialize_(false);
        if (fl_ == null) throw new IllegalStateException("file locator need to initialized first");
        rootDir_ = dir.endsWith("/") ? dir.substring(0, dir.length() - 1) : dir;
        File f = fl_.locate(rootDir_);
        if (!f.isDirectory()) throw new IllegalArgumentException("not a directory");
        if (logger_.isDebugEnabled())
            logger_.debug(String.format("root dir set to %1$s", dir));
    }

    @Override
    public void setUrlContextPath(String ctxPath) {
        if (null == ctxPath) throw new NullPointerException();
        if (ctxPath.endsWith("/")) ctxPath = ctxPath.substring(0, ctxPath.length() - 1);
        ctxPath_ = ctxPath;
    }

    @Override
    public void setCacheDir(File dir) {
        // comment below as inmemory configuration does not require dir to be exists
        // this is relevant when deploy app on readonly file system like heroku and gae
//        if (!dir.isDirectory() && !dir.mkdir())
//            throw new IllegalArgumentException("not a dir");
        checkInitialize_(false);
        cache_ = new FileCache(dir);
    }

    @Override
    public void setResourceUrlRoot(String urlRoot) {
        if (ctxPath_ == null) throw new IllegalStateException("ctxPath must be intialized first");
        if (!urlRoot.startsWith("/"))
            throw new IllegalArgumentException("url root must start with /");
        // checkInitialize_(false);
        if (!urlRoot.endsWith("/"))
            urlRoot = urlRoot + "/";

        resourceUrlRoot_ = urlRoot.startsWith(ctxPath_) ? urlRoot : ctxPath_ + urlRoot;
        if (logger_.isDebugEnabled())
            logger_.debug(String.format("url root set to %1$s", urlRoot));
    }

    @Override
    public void setResourceUrlPath(String urlPath) {
        checkInitialize_(false);
        if (null == resourceUrlRoot_) {
            throw new IllegalStateException("resourceUrlRoot must be initiated first");
        }
        if (!urlPath.endsWith("/"))
            urlPath = urlPath + "/";
        if (urlPath.startsWith("/")) {
            resourceUrlPath_ = urlPath.startsWith(ctxPath_) ? urlPath : ctxPath_ + urlPath;
        } else {
            resourceUrlPath_ = resourceUrlRoot_ + urlPath;
        }
        if (logger_.isDebugEnabled())
            logger_.debug(String.format("url path set to %1$s", urlPath));
    }

    @Override
    public void setCacheUrlPath(String urlPath) {
        checkInitialize_(false);
        if (null == resourceUrlRoot_) {
            throw new IllegalStateException("resourceUrlRoot must be initiated first");
        }
        if (!urlPath.endsWith("/"))
            urlPath = urlPath + "/";
        if (urlPath.startsWith("/")) {
            cacheUrlPath_ = urlPath.startsWith(ctxPath_) ? urlPath : ctxPath_ + urlPath;
        } else {
            cacheUrlPath_ = resourceUrlRoot_ + urlPath;
        }
        if (logger_.isDebugEnabled())
            logger_.debug(String.format("cache url root set to %1$s", urlPath));
    }

    @Override
    public void clearCache() {
        cache_.clear();
        processCache2_.clear();
        processCache_.clear();
    }

    private IFileLocator fl_ = FileResource.defFileLocator;

    @Override
    public void setFileLocator(IFileLocator fileLocator) {
        if (null == fileLocator)
            throw new NullPointerException();
        fl_ = fileLocator;
    }

    private IBufferLocator bl_ = new BufferLocator();

    @Override
    public void setBufferLocator(IBufferLocator bufferLocator) {
        if (null == bufferLocator)
            throw new NullPointerException();
        bl_ = bufferLocator;
    }

    private IRouteMapper rm_ = null;

    @Override
    public void setRouteMapper(IRouteMapper routeMapper) {
        if (null == routeMapper)
            throw new NullPointerException();
        rm_ = routeMapper;
    }

    private static final Pattern P_IMPORT = Pattern.compile("^\\s*@import\\s*\"(.*?)\".*");
    private Map<String, Set<File>> importsCache_ = new HashMap<String, Set<File>>();

    private Set<File> imports_(File file) {
        String key = "less_imports_" + file.getPath() + file.lastModified();

        Set<File> files = importsCache_.get(key);
        if (null == files) {
            files = new HashSet<File>();
            try {
                List<String> lines = fileToLines_(file);
                for (String line : lines) {
                    Matcher m = P_IMPORT.matcher(line);
                    while (m.find()) {
                        File f = new File(file.getParentFile(), m.group(1));
                        if (f.canRead()) {
                            files.add(f);
                            files.addAll(imports_(f));
                        }
                    }
                }
            } catch (Exception e) {
                if (logger_.isErrorEnabled())
                    logger_.error(String.format("Error occurred getting @imports from resource: $s", file), e);
            }
        }
        return files;
    }

    @Override
    public long getLastModified(File file) {
        long l = file.lastModified();
        if (ResourceType.CSS == type_) {
            // try to get last modified of all @imported files
            for (File f : imports_(file)) {
                l = Math.max(l, f.lastModified());
            }
        }
        return l;
    }

    @Override
    public void checkCache() {
        for (List<String> l : processCache_.keySet()) {
            for (String s : l) {
                if (isCDN_(s)) continue;
                File f = getFileFromURL_(s);
                if (null != f && f.exists()) {
                    long ts1 = getLastModified(f);
                    long ts2 = lastModifiedCache_.get(f);
                    if (ts1 > ts2) {
                        processCache_.remove(l);
                        break;
                    }
                }
            }
        }
    }

    private ConcurrentMap<List<String>, List<String>> processCache_ = new ConcurrentHashMap<List<String>, List<String>>();

    /**
     * A convention used by this minimizer is resource name suffix with
     * "_bundle". For any resource with the name suffix with "_bundle"
     */
    @Override
    public List<String> process(List<String> resourceNames) {
        checkInitialize_(true);
        if (resourceNames.isEmpty())
            return Collections.emptyList();
        if (minimize_ || ResourceType.CSS == type_) {
            if (useCache_ && processCache_.containsKey(resourceNames)) {
                // !!! cache of the return list instead of minimized file
                List<String> l = processCache_.get(resourceNames);
                if (null != l) return new ArrayList<String>(l);
            }
            // CDN items will break the resource name list into
            // separate chunks in order to keep the dependency order
            List<String> retLst = new ArrayList<String>();
            List<String> tmpLst = new ArrayList<String>();
            for (String fn : resourceNames) {
                if (!isCDN_(fn)) {
                    tmpLst.add(fn);
                } else {
                    if (tmpLst.size() > 0) {
                        retLst.add(minimize_(tmpLst));
                        tmpLst.clear();
                    }
                    retLst.add(fn);
                }
            }
            if (tmpLst.size() > 0) {
                retLst.add(minimize_(tmpLst));
                tmpLst.clear();
            }

            // return minimize_(resourceNames);
            processCache_.put(resourceNames, retLst);
            return retLst;
        } else {
            List<String> retLst = processWithoutMinimize(resourceNames);
            return retLst;
        }
    }

    private final String getExtension_(String path) {
        int pos = path.lastIndexOf(".");
        return -1 == pos ? "" : path.substring(pos, path.length());
    }

    private ConcurrentMap<List<String>, List<String>> processCache2_ = new ConcurrentHashMap<List<String>, List<String>>();

    @Override
    public List<String> processWithoutMinimize(List<String> resourceNames) {
        checkInitialize_(true);
        if (resourceNames.isEmpty())
            return Collections.emptyList();
        if (useCache_ && processCache2_.containsKey(resourceNames)) {
            // !!! cache of the return list instead of minimized file
            List<String> l = processCache2_.get(resourceNames);
            if (null != l) return new ArrayList<String>(l);
        }
        List<String> l = new ArrayList<String>();
        for (String fn : resourceNames) {
            if (isCDN_(fn))
                l.add(fn); // CDN resource
            else {
                String s = fn.replace(type_.getExtension(), "");
                File f = null;
                if (s.equalsIgnoreCase("default")
                        || s.endsWith(IDependenceManager.BUNDLE_SUFFIX)) {
                    continue;
                }

                f = getFile_(fn);
                if (null == f || !f.isFile()) {
                    continue;
                }

                String ext = getExtension_(f.getName());
                fn = fn.endsWith(ext) ? fn : fn + ext;

                fn = getUrl_(fn);

                l.add(fn);
            }
        }
        if (l.isEmpty()) {
            logger_.warn("Empty resource list found when processing " + resourceNames);
        }
        processCache2_.put(resourceNames, l);
        return l;
    }

    private String compress(String content) {
        try {
            Reader r = new StringReader(content);
            StringWriter w = new StringWriter();
            compressor_.compress(r, w);
            return w.toString();
        } catch (Exception e) {
            logger_.warn("error compress resource", e);
            return content;
        }
    }

    private void compress(File file, Writer out) {
        try {
            Reader r = new BufferedReader(new FileReader(file));
            try {
                compressor_.compress(r, out);
            } catch (Exception e) {
                logger_.warn("error compress resource " + file.getPath(), e);
                copy_(file, out);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void compress(String content, Writer out) {
        Reader r = new StringReader(content);
        try {
            compressor_.compress(r, out);
        } catch (Exception e) {
            logger_.warn("error compress resource", e);
            copy_(content, out);
        }
    }

    @Override
    public String processInline(String content) {
        if (!processInline_)
            return content;
        try {
            content = preprocess_(content);
            if (this.compress_) {
                return compress(content);
            } else {
                return content;
            }
        } catch (StackOverflowError e) {
            logger_.error("fatal error compressing inline content:" + e.getMessage());
            return content;
        } catch (Exception e) {
            logger_.error("error processing inline content", e);
            return content;
        }
    }

    @Override
    public String processStatic(File file) {
        String content = null;
        try {
            content = preprocess_(file);
        } catch (IOException e2) {
            logger_.error("error preprocess static file: " + file.getPath());
            return "";
        }
        try {
            if (this.compress_) {
                return compress(content);
            } else {
                return content;
            }
        } catch (StackOverflowError e) {
            logger_.error("fatal error compressing static file: " + file.getName());
            return content;
        } catch (Exception e) {
            logger_.warn("error processing static file: " + file.getPath(), e);
            try {
                return fileToString_(file);
            } catch (IOException e1) {
                return "";
            }
        }
    }

    private static String dos2unix_(String s) {
        return s.replaceAll("\r\n", "\n");
    }

    private String compileLess_(String s) throws LessException {
        return less_.compile(dos2unix_(s)).replace("\\n", "\n");
    }

    private String compileLess_(File f) throws LessException {
        return less_.compile(f).replace("\\n", "\n");
    }

    private String compileCoffee_(String s) throws JCoffeeScriptCompileException {
        return coffee_.compile(s);
    }

    private String compileCoffee_(File f) throws JCoffeeScriptCompileException, IOException {
        return compileCoffee_(fileToString_(f));
    }

    public IResource minimize(String resourceNames) {
        return minimize(decodeResourceNames(resourceNames));
    }

    private IResource minimize(List<String> resourceNames) {
        IResource rsrc = newCache_(resourceNames);
        Writer out = rsrc.getWriter();
        StringWriter sw = new StringWriter();
        try {
            for (String s : resourceNames) {
                // if (s.startsWith("http:")) l.add(s);
                if (isCDN_(s)) {
                    throw new IllegalArgumentException(
                            "CDN resource not expected in miminize method");
                }

                File f = getFileFromURL_(s);
                if (null != f && f.exists()) {
                    merge_(f, sw, s);
                } else {
                    // possibly a pseudo or error resource name
                }
            }
            String s = sw.toString();
            if (lessEnabled_() && postMergeLessCompile_()) {
                try {
                    s = compileLess_(s);
                } catch (LessException e) {
                    logger_.warn("Error compile less content: " + e.getMessage(), e);
                }
                if (compress_) {
                    try {
                        compress(s, out);
                    } catch (StackOverflowError e) {
                        logger_.error("fatal error compressing resource: " + e.getMessage());
                    }
                } else {
                    copy_(s, out);
                }
            } else {
                copy_(s, out);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger_.warn("cannot close output in minimizor", e);
                }
            }
        }

        return rsrc;
    }

    private String minimize_(List<String> resourceNames) {
        FileCache cache = cache_;

        if (useCache_) {
            String fn = cache.get(resourceNames);
            if (null != fn) {
                if (logger_.isDebugEnabled())
                    logger_.debug("cached file returned: " + fn);
                return cacheUrlPath_ + fn;
            }
        }

        IResource rsrc = minimize(resourceNames);

        String fn = rsrc.getKey();
        // filename always cached without regarding to cache setting
        // this is a good time to remove previous file
        // Note it's absolutely not a good idea to turn cache off
        // and minimize on in a production environment
        cache.put(resourceNames, fn);

        try {
            StringBuilder builder = new StringBuilder();
            builder.append(cacheUrlPath_);
            builder.append(fn);

            if (this.resourcesParam_ != null) {
                String resourcesParamValue = this.encodeResourceNames(resourceNames);
                if (resourcesParamValue != null) {
                    builder.append("?");
                    builder.append(this.resourcesParam_);
                    builder.append("=");
                    builder.append(URLEncoder.encode(resourcesParamValue, "utf8"));
                }
            }
            return builder.toString();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String encodeResourceNames(List<String> resourceNames) {
        StringBuilder builder = new StringBuilder();
        for (String resourceName : resourceNames) {
            resourceName = StringUtils.stripToNull(resourceName);
            if (resourceName != null) {
                if (builder.length() > 0) {
                    builder.append(',');
                }
                if (resourceName.startsWith(resourceUrlPath_)) {
                    resourceName = resourceName.substring(resourceUrlPath_.length());
                }
                builder.append(resourceName);
            }
        }
        return (builder.length() > 0) ? builder.toString() : null;
    }

    private List<String> decodeResourceNames(String resourceNames) {
        String[] names = resourceNames.split("[,]");
        if (names.length == 0) {
            return Collections.emptyList();
        }

        List<String> l = new ArrayList<String>(names.length);

        for (String name : names) {
            name = StringUtils.stripToNull(name);
            if (name != null) {
                if (!name.startsWith("/")) {
                    name = resourceUrlPath_ + name;
                }
                if (!l.contains(name)) {
                    l.add(name);
                }
            }
        }

        return l;
    }

    public static final String SYS_PROP_LESS_ENABLED = "greenscript.less.enabled";

    private boolean lessEnabled_() {
        if (ResourceType.CSS != type_)
            return false;
        boolean b = Boolean.parseBoolean(System.getProperty(
                SYS_PROP_LESS_ENABLED, "false"));
        return b;
    }

    public static final String SYS_PROP_COFFEE_ENABLED = "greenscript.coffee.enabled";

    private boolean coffeeEnabled_() {
        if (ResourceType.JS != type_) return false;
        boolean b = Boolean.parseBoolean(System.getProperty(SYS_PROP_COFFEE_ENABLED, "false"));
        return b;
    }

    /*
     * replace relative url inside the file content with absolute url. This is
     * because the compressed version file will be put in another folder
     *
     * @param s the content
     *
     * @param fn the original file name
     */
    private static final Pattern P_URL = Pattern.compile("url\\(['\"]?([^/'\"][^'\"]*?)['\"]?\\)", Pattern.CASE_INSENSITIVE | Pattern.CANON_EQ | Pattern.UNICODE_CASE);

    private String processRelativeUrl_(String s, String fn) throws IOException {
        if (ResourceType.CSS != type_)
            throw new IllegalStateException("not a css minimizer");

        if (this.rm_ != null) {
            fn = this.rm_.route(fn);
        }

        /*
         * Process fn: .../a.* -> .../
         */
        int p = fn.lastIndexOf("/") + 1;
        fn = (0 == p) ? resourceUrlPath_ : fn.substring(0, p);

        String prefix;
        if (fn.startsWith("/")) {
            if (fn.startsWith(resourceUrlPath_)) prefix = fn;
            else if (fn.startsWith(resourceUrlRoot_)) prefix = fn;
            else prefix = resourceUrlRoot_ + fn.replaceFirst("/", "");
        } else {
            prefix = resourceUrlPath_ + fn;
        }

        if (this.rm_ != null) {
            prefix = this.rm_.reverse(prefix);
        }

        try {
            Matcher m = P_URL.matcher(s);
            s = m.replaceAll("url(" + prefix + "$1)");
            return s;
        } catch (Throwable e) {
            System.err.println("Error process relative URL: " + fn);
            e.printStackTrace(System.err);
            return s;
        }
    }

    private String fileToString_(File f) throws IOException {
        BufferedReader r = new BufferedReader(new FileReader(f));
        String l = null;
        StringBuilder sb = new StringBuilder();
        String ls = System.getProperty("line.separator");
        while ((l = r.readLine()) != null) {
            sb.append(l);
            sb.append(ls);
        }
        r.close();
        return sb.toString();
    }

    private List<String> fileToLines_(File f) throws IOException {
        BufferedReader r = new BufferedReader(new FileReader(f));
        String l = null;
        List<String> lines = new ArrayList<String>();
        while ((l = r.readLine()) != null) {
            lines.add(l);
        }
        r.close();
        return lines;
    }

    private ConcurrentMap<File, Long> lastModifiedCache_ = new ConcurrentHashMap<File, Long>();

    private void merge_(File file, Writer out, String originalFn) {
        if (logger_.isTraceEnabled())
            logger_.trace("starting to minimize resource: " + file.getName());

        lastModifiedCache_.put(file, getLastModified(file));
        // possibly due to error or pseudo resource name
        try {
            String s = preprocess_(file, originalFn);
            if (compress_ && (!lessEnabled_() || !postMergeLessCompile_())) {
                if (logger_.isTraceEnabled())
                    logger_.trace(String.format("compressing %1$s ...",
                            file.getName()));
                if (null != s) {
                    compress(s, out);
                } else {
                    compress(file, out);
                }
            } else {
                if (null != s) copy_(s, out);
                else copy_(file, out);
            }
        } catch (IOException e) {
            logger_.warn(
                    "error processing javascript file file " + file.getName(),
                    e);
        }
    }

    private String preprocess_(String s) {
        if (lessEnabled_()) {
            try {
                s = compileLess_(s);
            } catch (Exception e) {
                logger_.warn("process inline content: " + e.getMessage());
            }
        }
        return s;
    }

    private boolean postMergeLessCompile_() {
        return Boolean.valueOf(System.getProperty("greenscript.lessCompile.postMerge", "false"));
    }

    private String preprocess_(File file) throws IOException {
        String s = null;
        if (lessEnabled_() && !postMergeLessCompile_()) {
            try {
                s = compileLess_(file);
            } catch (LessException e) {
                logger_.warn("error compile less file: " + file.getName() + ", error: " + e.getMessage(), e);
            }
        } else {
            if (file.getName().endsWith(".coffee")) {
                try {
                    s = coffee_.compile(fileToString_(file));
                } catch (JCoffeeScriptCompileException e) {
                    logger_.error("error compile coffee script file", e);
                }
            }
        }
        if (null == s) s = fileToString_(file);
        return s;
    }

    private String preprocess_(File file, String originalFn) throws IOException {
        String s = null;
        if (lessEnabled_() && !postMergeLessCompile_()) {
            try {
                s = compileLess_(file);
            } catch (LessException e) {
                logger_.warn("error compile less file: " + originalFn + ", error: " + e.getMessage());
            }
        } else if (coffeeEnabled_() && file.getName().endsWith(".coffee")) {
            try {
                s = compileCoffee_(file);
            } catch (JCoffeeScriptCompileException e) {
                logger_.error("error compile coffee script file", e);
            }
        }
        if (null == s) s = fileToString_(file);
        if (ResourceType.CSS == type_) s = processRelativeUrl_(s, originalFn);
        return s;
    }

    private String getUrl_(String resourceName) {
        String url = null;

        if (!"".equals(ctxPath_) && resourceName.startsWith(ctxPath_)) {
            url = resourceName;
        } else if (resourceName.startsWith("/")) {
            String s = ctxPath_ + resourceName;
            if (s.startsWith(resourceUrlRoot_)) {
                url = s;
            } else {
                url = resourceUrlRoot_ + resourceName.substring(1, resourceName.length());
            }
        } else {
            url = resourceUrlPath_ + resourceName;
        }

        return (this.rm_ != null) ? this.rm_.reverse(url) : url;
    }

    private File getFileFromURL_(String url) {
        return this.getFile_((this.rm_ != null) ? this.rm_.route(url) : url);
    }

    private File getFile_(String resourceName) {
        if (resourceName.startsWith("/") && !resourceName.startsWith(ctxPath_)) {
            resourceName = ctxPath_ + resourceName;
        }
        if (resourceName.startsWith(resourceUrlPath_)) {
            resourceName = resourceName.replaceFirst(resourceUrlPath_, "");
        } else if (resourceName.startsWith(resourceUrlRoot_)) {
            resourceName = resourceName.replaceFirst(resourceUrlRoot_, "/");
        }
        String fn = resourceName;
        String path;
        if (fn.startsWith("/")) {
            path = (!fn.startsWith(rootDir_)) ? rootDir_ + "/"
                    + fn.replaceFirst("/", "") : fn;
        } else {
            path = resourcePath_ + "/" + fn;
        }
        for (String ext : type_.getAllExtensions()) {
            String p = fn.endsWith(ext) ? path : path + ext;
            File f = fl_.locate(p);
            if (null != f) return f;
        }
        return null;
    }

    private static void copy_(File file, Writer out) throws IOException {
        if (logger_.isTraceEnabled())
            logger_.trace(String.format("merging file %1$s ...", file.getName()));
        copy_(new FileReader(file), out);
    }

    public static void copy_(Reader in, Writer out) {
        String line = null;
        BufferedReader r = null;
        try {
            r = new BufferedReader(in);
            PrintWriter w = new PrintWriter(out);
            while ((line = r.readLine()) != null) {
                w.println(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != r)
                try {
                    r.close();
                } catch (IOException e) {/*ignore*/}
        }
    }

    private static void copy_(String s, Writer out) {
        copy_(new StringReader(s), out);
    }

    private IResource newCache_(List<String> resourceNames) {
        if (inMemory_) {
            return bl_.newBuffer(resourceNames, type_.getExtension());
        } else {
            return new FileResource(newCacheFile_(resourceNames));
        }
    }

    private File newCacheFile_(List<String> resourceNames) {
        String extension = type_.getExtension();
        return cache_.createTempFile(resourceNames, extension);
    }

    private void checkInitialize_(boolean initialized) {
        boolean notInited = (resourcePath_ == null || rootDir_ == null
                || resourceUrlPath_ == null || cache_ == null || cacheUrlPath_ == null);

        if (initialized == notInited) {
            throw new IllegalStateException(
                    initialized ? "minimizer not initialized"
                            : "minimizer already initialized");
        }
    }

    public ResourceType getType() {
        return type_;
    }

    private final static Pattern P_CDN_PREFIX = Pattern.compile("^https?:");

    private final boolean isCDN_(String resourceName) {
        if (null == resourceName) return false;
        Matcher m = P_CDN_PREFIX.matcher(resourceName);
        return m.find();
    }

    public void setResourcesParam(String resourcesParam_) {
        this.resourcesParam_ = resourcesParam_;
    }

}
