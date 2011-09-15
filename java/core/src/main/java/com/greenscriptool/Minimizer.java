package com.greenscriptool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;
import com.greenscriptool.utils.BufferLocator;
import com.greenscriptool.utils.FileCache;
import com.greenscriptool.utils.FileResource;
import com.greenscriptool.utils.GreenScriptCompressor;
import com.greenscriptool.utils.IBufferLocator;
import com.greenscriptool.utils.ICompressor;

public class Minimizer implements IMinimizer {

    private static Log logger_ = LogFactory.getLog(Minimizer.class);

    private boolean minimize_;
    private boolean compress_;
    private boolean useCache_;
    private boolean inMemory_;
    private boolean processInline_;

    private FileCache cache_;
    private String resourceDir_;
    private String rootDir_;

    private String resourceUrlRoot_ = "";
    private String resourceUrlPath_;
    private String cacheUrlPath_;

    private ICompressor compressor_;
    private ResourceType type_;

    private LessEngine less_;

    public Minimizer(ResourceType type) {
        this(new GreenScriptCompressor(type), type);
    }

    @Inject
    public Minimizer(ICompressor compressor, ResourceType type) {
        if (null == compressor)
            throw new NullPointerException();
        compressor_ = compressor;
        type_ = type;
        less_ = new LessEngine();
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
        // if (!dir.isDirectory()) throw new
        // IllegalArgumentException("not a directory");
        checkInitialize_(false);
        resourceDir_ = dir;
    }

    @Override
    public void setRootDir(String dir) {
        // if (!dir.isDirectory()) throw new
        // IllegalArgumentException("not a directory");
//        if (!(dir.startsWith("/") || dir.startsWith(File.separator))) {
//            throw new IllegalArgumentException("root dir shall be start with /");
//        }
        checkInitialize_(false);
        rootDir_ = dir.replaceFirst("[/\\\\]$", "");
        if (logger_.isDebugEnabled())
            logger_.debug(String.format("root dir set to %1$s", dir));
    }

    @Override
    public void setCacheDir(File dir) {
        if (!dir.isDirectory() && !dir.mkdir())
            throw new IllegalArgumentException("not a dir");
        checkInitialize_(false);
        cache_ = new FileCache(dir);
    }

    @Override
    public void setResourceUrlRoot(String urlRoot) {
        if (!urlRoot.startsWith("/"))
            throw new IllegalArgumentException("url root must start with /");
        // checkInitialize_(false);
        if (!urlRoot.endsWith("/"))
            urlRoot = urlRoot + "/";
        resourceUrlRoot_ = urlRoot;
        if (logger_.isDebugEnabled())
            logger_.debug(String.format("url root set to %1$s", urlRoot));
    }

    @Override
    public void setResourceUrlPath(String urlPath) {
        if (!urlPath.startsWith("/"))
            throw new IllegalArgumentException("url path must start with /");
        checkInitialize_(false);
        if (!urlPath.endsWith("/"))
            urlPath = urlPath + "/";
        resourceUrlPath_ = urlPath;
        if (logger_.isDebugEnabled())
            logger_.debug(String.format("url path set to %1$s", urlPath));
    }

    @Override
    public void setCacheUrlPath(String urlPath) {
        if (!urlPath.startsWith("/"))
            throw new IllegalArgumentException(
                    "resource url path must start with /");
        checkInitialize_(false);
        if (!urlPath.endsWith("/"))
            urlPath = urlPath + "/";
        cacheUrlPath_ = urlPath;
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
    
    private static final Pattern P_IMPORT = Pattern.compile(".*@import\\s*\"(.*?)\".*"); 
    private Map<String, Set<File>> importsCache_ = new HashMap<String, Set<File>>();
    private Set<File> imports_(File file) {
        String key = "less_imports_" + file.getPath() + file.lastModified();
        
        Set<File> files = importsCache_.get(key);
        if (null == files) {
            files = new HashSet<File>();
            try {
                List<String> lines = fileToLines_(file);
                for (String line: lines) {
                    Matcher m = P_IMPORT.matcher(line);
                    while (m.find()) {
                        File f = new File(file.getParentFile(), m.group(1));
                        files.add(f);
                        files.addAll(imports_(f));
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
            for (File f: imports_(file)) {
                l = Math.max(l, f.lastModified());
            }
        }
        return l;
    }
    
    @Override
    public void checkCache() {
        for(List<String> l: processCache_.keySet()) {
            for (String s: l) {
                if (isCDN_(s)) continue;
                File f = getFile_(s);
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
                if (null != l) return l;
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
            if (null != l) return l;
        }
        List<String> l = new ArrayList<String>();
        String urlPath = resourceUrlPath_;
        for (String fn : resourceNames) {
            if (isCDN_(fn))
                l.add(fn); // CDN resource
            else {
                String s = fn.replace(type_.getExtension(), "");
                File f = null;
                if (s.equalsIgnoreCase("default")
                        || s.endsWith(IDependenceManager.BUNDLE_SUFFIX)) {
                    continue;
                } else {
                    f = getFile_(fn);
                    if (null == f || !f.isFile()) {
                        continue;
                    }
                }
                String ext = getExtension_(f.getName());
                fn = fn.endsWith(ext) ? fn : fn + ext;
                if (fn.startsWith("/")) {
                    if (!fn.startsWith(resourceUrlRoot_))
                        l.add(resourceUrlRoot_ + fn.replaceFirst("/", ""));
                    else
                        l.add(fn);
                } else
                    l.add(urlPath + fn);
            }
        }
        processCache2_.put(resourceNames, l);
        return l;
    }

    @Override
    public String processInline(String content) {
        if (!processInline_)
            return content;
        try {
            content = preprocess_(content);
            if (this.compress_) {
                Reader r = new StringReader(content);
                StringWriter w = new StringWriter();
                compressor_.compress(r, w);
                return w.toString();
            } else {
                return content;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String processStatic(File file) {
        try {
            String content = preprocess_(file, file.getPath());
            if (this.compress_) {
                Reader r = new StringReader(content);
                StringWriter w = new StringWriter();
                compressor_.compress(r, w);
                return w.toString();
            } else {
                return content;
            }
        } catch (Exception e) {
            logger_.warn("error processing static file: " + file.getPath(), e);
            try {
                return fileToString_(file);
            } catch (IOException e1) {
                return "";
            }
        }
    }

    private String minimize_(List<String> resourceNames) {
        FileCache cache = cache_;

        // List<String> l = new ArrayList<String>();
        if (useCache_) {
            String fn = cache.get(resourceNames);
            if (null != fn) {
                if (logger_.isDebugEnabled())
                    logger_.debug("cached file returned: " + fn);
                // l.add(cacheUrlPath_ + fn);
                return cacheUrlPath_ + fn;

                // for (String s: resourceNames) {
                // if (s.startsWith("http")) {
                // l.add(s);
                // }
                // }

                // return l;
            }
        }

        IResource rsrc = newCache_();
        Writer out = null;
        try {
            out = rsrc.getWriter();
            for (String s : resourceNames) {
                // if (s.startsWith("http:")) l.add(s);
                if (isCDN_(s))
                    throw new IllegalArgumentException(
                            "CDN resource not expected in miminize method");
                else {
                    File f = getFile_(s);
                    if (null != f && f.exists())
                        merge_(f, out, s);
                    else
                        ; // possibly a pseudo or error resource name
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    logger_.warn("cannot close output in minimizor", e);
                }
            }
        }

        String fn = rsrc.getKey();
        // filename always cached without regarding to cache setting
        // this is a good time to remove previous file
        // Note it's absolutely not a good idea to turn cache off
        // and minimize on in a production environment
        cache.put(resourceNames, fn);
        // l.add(cacheUrlPath_ + fn);
        // return l;
        return cacheUrlPath_ + fn;
    }

    public static final String SYS_PROP_LESS_ENABLED = "greenscript.less.enabled";

    private boolean lessEnabled_() {
        if (ResourceType.CSS != type_)
            return false;
        boolean b = Boolean.parseBoolean(System.getProperty(
                SYS_PROP_LESS_ENABLED, "false"));
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

        /*
         * Process fn: .../a.* -> .../
         */
        int p = fn.lastIndexOf("/") + 1;
        fn = 0 == p ? resourceUrlPath_ : fn.substring(0, p);

        String prefix;
        if (fn.startsWith("/")) {
            prefix = fn.startsWith(resourceUrlRoot_) ? fn : resourceUrlRoot_ + fn.replaceFirst("/", "");
        } else {
            prefix = resourceUrlPath_ + fn;
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

//    private String processRelativeUrl_(File f, String originalFn)
//            throws IOException {
//        String s = fileToString_(f);
//        return processRelativeUrl_(s, originalFn);
//    }

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
            if (compress_) {
                try {
                    if (logger_.isTraceEnabled())
                        logger_.trace(String.format("compressing %1$s ...",
                                file.getName()));
                    Reader r = null;
                    r = null != s ? new StringReader(s) : new BufferedReader(new FileReader(file));
                    compressor_.compress(r, out);
                } catch (Exception e) {
                    logger_.warn(
                            String.format("error minimizing file %1$s",
                                    file.getName()), e);
                    copy_(file, out);
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
                s = less_.compile(s);
            } catch (Exception e) {
                logger_.warn("process inline content", e);
            }
        }
        return s;
    }
    
    private String preprocess_(File file, String originalFn) throws IOException {
        String s = fileToString_(file);
        if (ResourceType.CSS == type_) {
            if (lessEnabled_()) {
                try {
                    s = less_.compile(s);
                } catch (LessException e) {
                    logger_.warn("error compile less file: " + originalFn, e);
                }
            }
            s = processRelativeUrl_(s, originalFn);
        }
        return s;
    }

    private File getFile_(String resourceName) {
        if (resourceName.startsWith(resourceUrlPath_)) {
            resourceName = resourceName.replaceFirst(resourceUrlPath_, "");
        }
        String fn = resourceName;
        String path;
        if (fn.startsWith("/")) {
            path = (!fn.startsWith(rootDir_)) ? rootDir_ + "/"
                    + fn.replaceFirst("/", "") : fn;
        } else {
            path = rootDir_ + "/" + resourceDir_ + "/" + fn;
        }
        for (String ext: type_.getAllExtensions()) {
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
    
    private static void copy_(Reader in, Writer out) throws IOException {
        String line = null;
        BufferedReader r = null;
        try {
            r = new BufferedReader(in);
            PrintWriter w = new PrintWriter(out);
            while ((line = r.readLine()) != null) {
                w.println(line);
            }
        } finally {
            if (null != r)
                r.close();
        }
    }
    
    private static void copy_(String s, Writer out) throws IOException {
        copy_(new StringReader(s), out);
    }

    private IResource newCache_() {
        if (inMemory_) {
            return bl_.newBuffer(type_.getExtension());
        } else {
            return new FileResource(newCacheFile_());
        }
    }

    private File newCacheFile_() {
        String extension = type_.getExtension();
        return cache_.createTempFile(extension);
    }

    private void checkInitialize_(boolean initialized) {
        boolean notInited = (resourceDir_ == null || rootDir_ == null
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

}
