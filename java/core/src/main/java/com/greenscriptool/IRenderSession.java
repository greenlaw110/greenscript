package com.greenscriptool;

import java.util.List;
import java.util.Set;

/**
 * A session refers to the rendering process for one HTML page. The process might involve 
 * parsing multiple templates in which GreenScript tool is invoked to declare and/or load
 * resources to output:
 * <ul>
 * <li>Declare. During parsing templates, tag notifies greenscript that a certain list of
 *     resources is required to be loaded (output later)</li>
 * <li>Load. tag ask greenscript to load the declared resources for output. this could have
 *     several options:
 *     <ul>
 *     <li>with-dependencies: output current declared resources along with all their
 *         dependencies</li>
 *     <li>all: output current declared resources and all previously declared resources
 *         that are not output yet</li>
 *     </ul>
 * </ul>
 * 
 * An instance of {@link IRenderSession} is the interface provided to tag libraries. It 
 * might use {@link IDependenceManager} to comprehend a resource list and feed  
 * that list to {@link IProcessor#process(List, ResourceType)} to get the
 * final resource list for output
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0.1 2010-11-13 add compatibility for play-greenscript v1.1 or before
 * @version 1.0 2010-10-14 original version
 * @since 1.0
 */
public interface IRenderSession {
    
    public static final String DEFAULT = "__def__";
    
    /**
     * @see IDependenceManager#SEPARATOR
     */
    final String SEPARATOR = IDependenceManager.SEPARATOR;

    /**
     * <p>Notify render session that a list of resources are
     * declared to be required.</p>
     * 
     * <p><code>media</code> and <code>browser</code> are at
     * the moment used to declare css resources only. Both
     * are optional.</p>
     * 
     * @param nameList resource names separated by {@link #SEPARATOR}
     * @param media css resource target media
     * @param browser browser to which css resource apply to 
     */
    void declare(String nameList, String media, String browser);
    void declare(List<String> nameList, String media, String browser);
    
    /**
     * <p>Notify render session of an inline resource (javascript/css) and it's
     * priority level. Inline content with higher priority is guaranteed to be
     * output before inline contents with lower priority</p>
     * 
     * <p>inline resource apply to default context only</p>
     * 
     * <p>note that the output order of line content is not guaranteed. Meaning
     * caller shall not make assumption on the dependencies of inline content
     * declared</p>
     * 
     * @param inline the inline content
     * @param priority indicate priority of inline content 
     */
    void declareInline(String inline, int priority);
    
    /**
     * <p>Ask session manager to comprehend a resource list with given
     * resource list and options. The resources in the list is subject
     * to output to HTML page. "comprehend" here means comprehend
     * of {@link IDependenceManager#comprehend(List, boolean)} plus  
     * process of {@link IProcessor#process(List, ResourceType)}</p>
     * 
     * <p>Once a resource has been loaded, then it shall not be loaded
     * again in the same session.</p> 
     * 
     * @param nameList, the resource list to be output to HTML page 
     *        (comprehend into the return list)
     * @param withDependencies, if true then load all dependencies 
     *        of the resources declared in the name list
     * @param all, if true then indicate load all resources (declared 
     *        this time and previously, plus all dependencies)
     * @param media. If specified then only resources declared
     *        target this media will be loaded. If omitted, then only
     *        resources not declared to be associated with any media
     *        will be loaded.
     * @param browser. If specified then only resources declared
     *        target this browser will be loaded. If omitted, then only
     *        resources not declared to be associated with any browser
     *        will be loaded.
     * @return a list of URLs point to the resources after processed
     */
    List<String> output(String nameList, boolean withDependencies, boolean all, String media, String browser);
    
    /**
     * Output all inline contents been declared. Note any inline content
     * been declared shall be output only once
     * 
     * @return
     */
    String outputInline();
    
    /**
     * Return all medias declared as long as browser specified. 
     * Note {@link #DEFAULT default} media shall not be returned
     * 
     * @param browser the browser declared at the same time with the media. If <code>null</code>
     *        passed in, then {@link #DEFAULT default} browser shall be used to query media set
     * @return medias declared during this session using {@link #declare(String, String, String)}
     */
    Set<String> getMedias(String browser);
    
    /**
     * Return all browsers declared. Note {@link #DEFAULT default}
     * browser shall not be returned
     * 
     * @return all browsers declared during this session using {@link #declare(String, String, String)}
     */
    Set<String> getBrowsers();
    
    /**
     * Return true if there are resources declared in this session
     *  
     * @return
     */
    boolean hasDeclared();
}
