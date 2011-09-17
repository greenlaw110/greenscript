package com.greenscriptool;

import java.io.File;
import java.util.List;

import com.greenscriptool.utils.IBufferLocator;

/**
 * <p>an <code>IMinimizer</code> is a specialized {@link IProcessor} in that it
 * can merge/compress resources.</p>
 * 
 * <p>An minimizer is used in GreenScript utility to provide minimizing function, which
 * is controlled by the following settings:</p>
 * <ol>
 * <li><code>minimize</code>, to enable/disable the minimizing of resource files. If 
 *     <code>minimize</code> is enabled then a list of resource files can be merged into
 *     a single file</li>
 * <li><code>compress</code>, to enable/disable compression of resource files. If
 *     <code>compress</code> is enabled then the file will be compressed during minimizing
 *     (merging) process. <code>compress</code> setting has no effect when <code>minimize</code>
 *     setting is disabled</li>
 * <li><code>cache</code>, to enable/disable cache of minimizing. If <code>cache</code> is
 *     enabled then the minimizing result will be cached and can be reused when next time
 *     minimizing process invoked on the same resource file list. <code>cache</code> has
 *     no effect when <code>minimize</code> is disabled</li>
 * </ol>
 * 
 * <p>The client program call {@link #process(List)} to invoke minimizing
 * process on a give list of filenames.</p>
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0, 2010-10-16
 * @since 1.0
 */
public interface IMinimizer extends IProcessor {
    
    /**
     * <p>Enable or Disable minimize</p>
     * 
     * <p>If <code>minimize</code> is enabled then calling {@link #process(List)} will
     * merge all the resources specified in the fileName list, and return the name
     * of the merged file.</p>
     * 
     * <p>If <code>minimize</code> is disabled then calling {@link #process(List)}
     * will not merge/compress resources</p>
     * 
     * @param enable true to enable minimize, false to disable it
     */
    void enableDisableMinimize(boolean enable);
    
    /**
     * <p>Enable or Disable compress</p>
     * 
     * <p>If <code>compress</code> is enabled then calling {@link #process(List)} will
     * compress resources during merge process</p>
     * 
     * <p>If <code>compress</code> is disabled then calling {@link #process(List)}
     * shall not compress resource</p>
     * 
     * <p><code>compress</code> setting has no effect if <code>minimize</code> is disabled</p>
     * 
     * <p>Change <code>compress</code> setting shall clear the minimize cache so that the later
     * calling to {@link #process(List)} will not use previous cached file, which
     * might not be corresponding to the <code>compress</code> setting</p>
     * 
     * @param enable true to enable compress, false to disable it
     */
    void enableDisableCompress(boolean enable);
    
    /**
     * <p>Enable or Disable cache</p>
     * 
     * <p>If <code>cache</code> is enabled then calling {@link #process(List)} will
     * shall reuse the cached result of the minimizing process (if there is) on the same resource
     * name list.</p>
     * 
     * <p>If <code>cache</code> is disabled then calling {@link #process(List)}
     * shall always re-execute the minimizing process on the list without regarding to the
     * existence of cached result</p>
     * 
     * <code>cache</code> setting has no effect if <code>minimize</code> is disabled
     * 
     * @param enable true to enable cache, false to disable it
     */
    void enableDisableCache(boolean enable);
    
    /**
     * <p>Enable or Disable in memory cache</p>
     * 
     * <p>If <code>inMemoryCache</code> is enabled then the result of {@link #process(List)}
     * shall be stored in memory buffer instead of a temporary file
     * 
     * <p><code>InMemoryCache</code> option has no effect when <code>cache</code> is disabled
     */
    void enableDisableInMemoryCache(boolean inMemory);
    
    /**
     * <p>Enable or disable process inline code</p>
     * 
     * <p>If this configuration is turned on then further process of inline
     * code will be conducted, including: 1. use Less engine to compile
     * css code, 2. use compressor to compress js and css code.</p>
     * 
     * @param b
     */
    void enableDisableProcessInline(boolean b);
    
    /**
     * Return <code>minimize</code> setting
     * 
     * @return true if <code>minimize</code> is enabled, false otherwise
     */
    boolean isMinimizeEnabled();
    
    /**
     * Return <code>compress</code> setting
     * 
     * @return true if <code>compress</code> is enabled, false otherwise
     */
    boolean isCompressEnabled();
    
    /**
     * Return <code>cache</code> setting
     * 
     * @return true if <code>cache</code> is enabled, false otherwise
     */
    boolean isCacheEnabled();
    
    /**
     * Set directory where minimizer can find resource if the 
     * resource name does not start with "/".
     * 
     * @param dir the directory resource is stored
     */
    void setResourceDir(String dir);
    
    /**
     * Set the directory where minimizer can find resource if
     * the resource name start with "/"
     * 
     * @param root the root directory of all kind of resource types
     */
    void setRootDir(String root);
    
    /**
     * Set the directory where minimizer can store the processed resource files
     * 
     * @param dir where the processed resource file stored
     */
    void setCacheDir(File dir);
    
    /**
     * <p>Set the url path of raw resources (those not processed). 
     * The <code>urlPath</code> specified must start with "/". Note url 
     * path only used along with local resources, for remote resources 
     * (start with "http"), the resource url path will not be used.</p>
     * 
     * <p>Note, the urlPath should map to resource directory set by  
     * {@link #setResourceDir(File)}, otherwise, 404 error might
     * occurred when user requests the raw resource</p>
     * 
     * @param urlPath
     */
    void setResourceUrlPath(String urlPath);
    
    /**
     * <p>Set the resource URL root (corresponding to {@link #setRootDir(String)}).
     * 
     * @param urlRoot
     */
    void setResourceUrlRoot(String urlRoot);
    
    void setUrlContextPath(String ctxPath);
    
    /**
     * <p>Set the url path of processed resources. The <code>urlPath</code> 
     * specified must start with "/".</p> 
     * 
     * <p>Note, the url path should map to cache directory set by  
     * {@link #setCacheDir(File)}, otherwise, 404 error might
     * occurred when user requests the processed resource</p>
     * 
     * @param urlPath the url root to fetch the resources been
     *        processed
     */
    void setCacheUrlPath(String urlPath);
    
    /**
     * <p>Call this method to check the cached file last-modified timestamp.
     * if the timestamp is newer then the cached content will be cleared 
     */
    void checkCache();
    
    /**
     * A management interface to notify miminizer to clear cached processed resources. 
     */
    void clearCache();
    
    /**
     * Get last-modified timestamp of the resource specified and all imported resources
     * if there is
     * 
     * @param resource
     * @return
     */
    long getLastModified(File resource);
    
    /**
     * <p>process a given list of file names and return urls point to the processed resources</p>
     * 
     * <p>Note pseudo resources (bundle without real resource peer) might be put into the 
     * resource name list. In case a pseudo resource encountered, the process will simply
     * ignore it. This is also true for real resource with incorrect resource name put in.
     * Meaning for any resource name, if no real resource can be found, then minimize will
     * silently ignore them. The implementation might consider logging an error item for 
     * this case.</p>
     * 
     * <p>For some resources it's not possible to minimize it, e.g. CDN distributed javascripts
     * or stylesheets. These resources will always be put into the returning list and these
     * resources are guaranteed to be always prior to the native resources in the returning list</p>
     * 
     * <p>If {@link #isMinimizeEnabled()} returns true, then all resources specified by the
     * resource name list will be merged together. If {@link #isCompressEnabled()} returns
     * true, then the resources will be compressed during merge process. If {@link #isCacheEnabled()}
     * returns true, then a cached minimizing resource will be returned if the resource name
     * list matches. The final minimized resource name will be returned in a String list which
     * contains only one item</p>
     * 
     * <p>If {@link #isMinimizeEnabled()} returns false, then all resource names will tested if
     * a real resource exists. If a real resource exists, then the corresponding resource name
     * will added into the returning list. The order of the resource names will be kept unchanged</p>
     * 
     * @param resourceNames a list of resource names
     * @return A list contains urls of either non-native resource names and the minimized  
     *         resource name if minimize is enabled. Or all resource urls corresponding  
     *         to a real resource if minimize is disabled. An empty list will be returned  
     *         if the resourceNames list is empty.
     * 
     * @throws NullPointerException if fileNames is null
     */
    List<String> process(List<String> resourceNames);
    
    /**
     * <p>process a give list of file names and return urls point to the processed resources</p>
     * 
     * @param resourceNames
     * @return
     */
    List<String> processWithoutMinimize(List<String> resourceNames);
    
    /**
     * <p>process inline text
     * 
     * @param text
     * @return
     * @since 1.2.6
     */
    String processInline(String text);
    
    /**
     * <p>process static content</p>
     * 
     * @param file
     * @return
     */
    String processStatic(File file);
    
    /**
     * Set file locator
     * @param fileLocator
     */
    void setFileLocator(IFileLocator fileLocator);
    
    void setBufferLocator(IBufferLocator bufferLocator);
}
