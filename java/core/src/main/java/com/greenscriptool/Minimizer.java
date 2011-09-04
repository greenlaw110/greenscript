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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asual.lesscss.LessEngine;
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
        minimize_ = enable;
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
        return minimize_;
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
        checkInitialize_(false);
        rootDir_ = dir.endsWith(File.separator) ? dir : dir + File.separator;
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

    private ConcurrentMap<List<String>, List<String>> processCache_ = new ConcurrentHashMap<List<String>, List<String>>();
    private ConcurrentMap<List<String>, List<String>> processCache2_ = new ConcurrentHashMap<List<String>, List<String>>();

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
                return processCache_.get(resourceNames);
            }
            // CDN items will break the resource name list into
            // separate chunks in order to keep the dependency order
            List<String> retLst = new ArrayList<String>();
            List<String> tmpLst = new ArrayList<String>();
            for (String fn : resourceNames) {
                if (!fn.startsWith("http")) {
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

    @Override
    public List<String> processWithoutMinimize(List<String> resourceNames) {
        checkInitialize_(true);
        if (resourceNames.isEmpty())
            return Collections.emptyList();
        if (useCache_ && processCache2_.containsKey(resourceNames)) {
            // !!! cache of the return list instead of minimized file
            return processCache2_.get(resourceNames);
        }
        List<String> l = new ArrayList<String>();
        String urlPath = resourceUrlPath_;
        for (String fn : resourceNames) {
            if (fn.startsWith("http"))
                l.add(fn); // CDN resource
            else {
                String s = fn.replace(type_.getExtension(), "");
                if (s.equalsIgnoreCase("default")
                        || s.endsWith(IDependenceManager.BUNDLE_SUFFIX)) {
                    continue;
                } else {
                    File f = getFile_(fn);
                    if (null == f || !f.isFile()) {
                        continue;
                    }
                }
                String ext = type_.getExtension();
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
    public String processInline(String text) {
        if (!processInline_) return text;
        try {
            if (lessEnabled_()) {
                text = less_.compile(text);
            }
            if (this.compress_) {
                Reader r = new StringReader(text);
                StringWriter w = new StringWriter();
                compressor_.compress(r, w);
                return w.toString();
            } else {
                return text;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                if (s.startsWith("http:"))
                    throw new IllegalArgumentException(
                            "CDN resource not expected in miminize method");
                else {
                    if (s.startsWith(resourceUrlPath_)) {
                        s = s.replaceFirst(resourceUrlPath_, "");
                    }
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
    private static final Pattern P_URL = Pattern.compile(
            "url\\('?([^/].*)'?\\)", Pattern.CASE_INSENSITIVE
                    | Pattern.CANON_EQ | Pattern.UNICODE_CASE);

    private String processRelativeUrl_(String s, String fn) throws IOException {
        if (ResourceType.CSS != type_)
            throw new IllegalStateException("not a css minimizer");

        /*
         * Process fn: .../a.* -> .../
         */
        int p = fn.lastIndexOf("/") + 1;
        fn = fn.substring(0, p);

        String prefix;
        if (fn.startsWith("/")) {
            prefix = fn.startsWith(resourceUrlRoot_) ? resourceUrlRoot_
                    + fn.replaceFirst("/", "") : fn;
        } else {
            prefix = resourceUrlPath_ + fn;
        }
        Matcher m = P_URL.matcher(s);
        return m.replaceAll("url(" + prefix + "$1)");
    }

    private String processRelativeUrl_(File f, String originalFn)
            throws IOException {
        String s = fileToString_(f);
        return processRelativeUrl_(s, originalFn);
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

    private void merge_(File file, Writer out, String originalFn) {
        if (logger_.isTraceEnabled())
            logger_.trace("starting to minimize resource: " + file.getName());

        // possibly due to error or pseudo resource name
        try {
            if (compress_) {
                try {
                    if (logger_.isTraceEnabled())
                        logger_.trace(String.format("compressing %1$s ...",
                                file.getName()));
                    Reader r = null;
                    // do less compile for css
                    if (lessEnabled_()) {
                        String s = less_.compile(file);
                        s = processRelativeUrl_(s, originalFn);
                        r = new StringReader(s);
                    } else {
                        if (ResourceType.CSS == type_) {
                            String s = processRelativeUrl_(file, originalFn);
                            r = new StringReader(s);
                        } else {
                            r = new BufferedReader(new FileReader(file));
                        }
                    }
                    compressor_.compress(r, out);
                } catch (Exception e) {
                    logger_.warn(
                            String.format("error minimizing file %1$s",
                                    file.getName()), e);
                    copy_(file, out);
                }
            } else {
                copy_(file, out);
            }
        } catch (IOException e) {
            logger_.warn(
                    "error processing javascript file file " + file.getName(),
                    e);
        }
    }

    private File getFile_(String resourceName) {
        String fn = resourceName, ext = type_.getExtension();
        fn = fn.endsWith(ext) ? fn : fn + ext;
        String path;
        if (fn.startsWith("/")) {
            path = (!fn.startsWith(rootDir_)) ? rootDir_
                    + fn.replaceFirst("/", "") : fn;
        } else {
            path = rootDir_ + File.separator + resourceDir_ + File.separator
                    + fn;
        }
        File f = fl_.locate(path);
        // System.out.println(">>>" + f.getAbsolutePath());
        return f;
    }

    private static void copy_(File file, Writer out) throws IOException {
        if (logger_.isTraceEnabled())
            logger_.trace(String.format("merging file %1$s ...", file.getName()));
        String line = null;
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(file));
            PrintWriter writer = new PrintWriter(out);
            while ((line = r.readLine()) != null) {
                writer.println(line);
            }
        } finally {
            if (null != r)
                r.close();
        }
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

}
