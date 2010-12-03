package com.greenscriptool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.greenscriptool.utils.FileCache;
import com.greenscriptool.utils.GreenScriptCompressor;
import com.greenscriptool.utils.ICompressor;

public class Minimizer implements IMinimizer {
    
    /**
     *  <p>Used to mark a resource being a bundle resource. A bundle resource
     *  is not a real resource, instead it represents a groups of resources
     *  by means of {@link IDependenceManager dependency management}.</p>
     *  
     *  <p>A resource marked with this surffix will be checked to see if it
     *  exists, if not, then no processing will be taken on the resource</p> 
     */
    public static final String BUNDLE_SUFFIX = "_bundle";
    
    private static Log logger_ = LogFactory.getLog(Minimizer.class);
    
    private boolean minimize_;
    private boolean compress_;
    private boolean useCache_;
    
    /**
     * if this flag set to true, then minimizer will try to verify
     * each resource, test if the resource exists during processing
     */
    private boolean verifyResource_;
    
    private FileCache cache_;
    private File resourceDir_;
    private File rootDir_;
    
    private String resourceUrlPath_;
    private String cacheUrlPath_;
    
    private ICompressor compressor_;
    private ResourceType type_;
    
    public Minimizer(ResourceType type) {
        this(new GreenScriptCompressor(type), type);
    }
    
    @Inject
    public Minimizer(ICompressor compressor, ResourceType type) {
        if (null == compressor) throw new NullPointerException();
        compressor_ = compressor;
        type_ = type;
    }

    @Override
    public void enableDisableMinimize(boolean enable) {
        minimize_ = enable;
        if (logger_.isDebugEnabled()) logger_.debug("minimize " + (enable ? "enabled" : "disabled"));
    }

    @Override
    public void enableDisableCompress(boolean enable) {
        compress_ = enable;
        if (logger_.isDebugEnabled()) logger_.debug("compress " + (enable ? "enabled" : "disabled"));
        clearCache();
    }

    @Override
    public void enableDisableCache(boolean enable) {
        useCache_ = enable;
        if (logger_.isDebugEnabled()) logger_.debug("cache " + (enable ? "enabled" : "disabled"));
    }
    
    public void enableDisableVerifyResource(boolean verify) {
        verifyResource_ = verify;
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
    public void setResourceDir(File dir) {
        if (!dir.isDirectory()) throw new IllegalArgumentException("not a directory");
        checkInitialize_(false);
        resourceDir_ = dir;
    }
    
    @Override
    public void setRootDir(File dir) {
        if (!dir.isDirectory()) throw new IllegalArgumentException("not a directory");
        checkInitialize_(false);
        rootDir_ = dir;
        if (logger_.isDebugEnabled()) logger_.debug(String.format("root dir set to %1$s", dir));
    }
    
    @Override
    public void setCacheDir(File dir) {
        if (!dir.isDirectory() && !dir.mkdir()) throw new IllegalArgumentException("not a dir");
        checkInitialize_(false);
        cache_ = new FileCache(dir);
    }
    
    @Override
    public void setResourceUrlPath(String urlPath) {
        if (!urlPath.startsWith("/")) throw new IllegalArgumentException("url path must start with /");
        checkInitialize_(false);
        if (!urlPath.endsWith("/")) urlPath = urlPath + "/";
        resourceUrlPath_ = urlPath;
        if (logger_.isDebugEnabled()) logger_.debug(String.format("url root set to %1$s", urlPath));
    }
    
    @Override
    public void setCacheUrlPath(String urlPath) {
        if (!urlPath.startsWith("/")) throw new IllegalArgumentException("resource url path must start with /");
        checkInitialize_(false);
        if (!urlPath.endsWith("/")) urlPath = urlPath + "/";
        cacheUrlPath_ = urlPath;
        if (logger_.isDebugEnabled()) logger_.debug(String.format("cache url root set to %1$s", urlPath));
    }
    
    @Override
    public void clearCache() {
        cache_.clear();
    }
    
    /**
     * A convention used by this minimizer is resource name suffix with "_bundle". For
     * any resource with the name suffix with "_bundle"
     */
    @Override
    public List<String> process(List<String> resourceNames) {
        checkInitialize_(true);
        if (resourceNames.size() == 0) return Collections.emptyList();
        if (minimize_) {
            return minimize_(resourceNames);
        } else {
            List<String> l = new ArrayList<String>();
            String urlPath = resourceUrlPath_;
            for (String fn: resourceNames) {
                if (fn.startsWith("http")) l.add(fn); // CDN resource
                else {
                    String s = fn.replace(type_.getExtension(), "");
                    if (verifyResource_ || s.equalsIgnoreCase("default") || s.endsWith(BUNDLE_SUFFIX)) {
                        File f = getFile_(fn);
                        if (!f.isFile()) continue;
                    }
                    String ext = type_.getExtension();
                    fn = fn.endsWith(ext) ? fn : fn + ext; 
                    if (fn.startsWith("/")) l.add(fn);
                    else l.add(urlPath + fn);
                }
            }
            return l;
        }
    }
    
    private List<String> minimize_(List<String> resourceNames) {
        FileCache cache = cache_;
        
        List<String> l = new ArrayList<String>();
        if (useCache_) {
            String fn = cache.get(resourceNames);
            if (null != fn) {
                if (logger_.isDebugEnabled())
                    logger_.debug("cached file returned: " + fn);
                l.add(cacheUrlPath_ + fn);
                
                for (String s: resourceNames) {
                    if (s.startsWith("http")) {
                        l.add(s);
                    }
                }
                
                return l;
            }
        }
        
        File outFile = newCacheFile_();
        Writer out = null;
        try {
            out = new BufferedWriter(new FileWriter(outFile, true));
            for (String s: resourceNames) {
                if (s.startsWith("http:")) l.add(s);
                else {
                    File f = getFile_(s);
                    if (f.exists()) merge_(f, out);
                    else ; // possibly a pseudo or error resource name
                }
            }
        } catch (IOException e) {
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
        
        String fn = outFile.getName();
        // filename always cached without regarding to cache setting
        // this is a good time to remove previous file
        // Note it's absolutely not a good idea to turn cache off
        // and minimize on in a production environment
        cache.put(resourceNames, fn);
        l.add(cacheUrlPath_ + fn);
        return l;
    }
    
    private void merge_(File file, Writer out) {
        if (logger_.isTraceEnabled()) logger_.trace("starting to minimize resource: " + file.getName());
        
        // possibly due to error or pseudo resource name
        try {
            if (compress_) {
                try {
                    compressor_.compress(file, out);
                } catch (Exception e) {
                    logger_.warn(String.format("error minimizing file %1$s", file.getName()), e);
                    copy_(file, out);
                }
            } else {
                copy_(file, out);
            }
        } catch (IOException e) {
            logger_.warn("error processing javascript file file " + file.getName(), e);
        }
    }

    private File getFile_(String resourceName) {
        String fn = resourceName, ext = type_.getExtension();
        fn = fn.endsWith(ext) ? fn : fn + ext;
        if (fn.startsWith("/")) return new File(rootDir_, fn.substring(1));
        return new File(resourceDir_, fn);
    }

    private static void copy_(File file, Writer out) throws IOException {
        if (logger_.isTraceEnabled()) logger_.trace(String.format("merging file %1$s ...", file.getName()));
        String line = null;
        BufferedReader r = null;
        try {
            r = new BufferedReader(new FileReader(file));
            PrintWriter writer = new PrintWriter(out);
            while ((line = r.readLine()) != null) {
                writer.println(line);
            }
        } finally {
            if (null != r) r.close();
        }
    }

    private File newCacheFile_() {
        String extension = type_.getExtension(); 
        return cache_.createTempFile(extension);
    }
    
    private void checkInitialize_(boolean initialized) {
        boolean notInited = (resourceDir_ == null || rootDir_ == null || resourceUrlPath_ == null || cache_ == null || cacheUrlPath_ == null); 
        
        if (initialized == notInited) {
            throw new IllegalStateException(initialized ?  "minimizer not initialized" : "minimizer already initialized");
        }
    }

    public ResourceType getType() {
        return type_;
    }

}
