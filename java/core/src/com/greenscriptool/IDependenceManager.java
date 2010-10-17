package com.greenscriptool;

import java.util.Collection;
import java.util.List;

/**
 * Define a dependency manager interface.
 * 
 * A class that implement <code>IDependencyManager</code> provides the
 * capability to build up and manage resource dependencies. It can also 
 * {@link #comprehend(Collection)} a list of resources by adding missing
 * dependent resources and sort the list following the dependent chain 
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0, 2010-10-16
 * @since 1.0
 */
public interface IDependenceManager {
    
    /**
     * <code>SEPARATOR</code> defines separator characters to split a
     * resource name list in {@link java.lang.String}
     */
    static final String SEPARATOR = "[ ,;]";
    
    /**
     * <code>DEFAULT</code> denotes a pseudo resource called "default". all resources
     * that are dependsOn of "default" resource will be loaded into the 
     * {@link #comprehend(List) comprehended list} before all other resources if 
     * <code>withDefault</code> option set to true
     * 
     */
    static final String DEFAULT = "default";

    /**
     * Calling this method is equal to calling {@link #comprehend(Collection, boolean)}
     * with <code>withDefault</code> set to <code>false</code>
     *  
     * @param resourceNames a collection of resource names
     * @return comprehended resource name list
     */
    List<String> comprehend(Collection<String> resourceNames);
    
    /**
     * Comprehend a resource name list. This process includes:
     * <ol>
     * <li>Add missing dependent resource names into the list</li>
     * <li>Sort the list following the dependency graph built up in
     *     the <code>IDependencyManager</code>. the order rule makes
     *     sure that for any resource, the depend on resources of it
     *     are always put before that resource in the list</li>
     * <li>Duplicated items be removed from the list
     * </ol>
     * 
     * if <code>withDefault</code> option set to true, then all dependOns
     * of {@link #DEFAULT} resource and their dependOns in turn will be added 
     * to the comprehended list before any other resources
     * 
     * if there are any resources in the list which are not managed by
     * this {@link IDependenceManager}, ie. there is no dependence 
     * relationship associated with the resources, then these names
     * will be put at the end of the returned list, following their
     * original order. Again duplicated items will be removed
     * 
     * @param resourceNames a collection of resource names
     * @param withDefault indicate whether add {@link #DEFAULT} resource and
     *        it's depend on resources into the comprehended list
     * @return
     */
    List<String> comprehend(Collection<String> resourceNames, boolean withDefault);
    
    /**
     * Calling this method is equal to calling {@link #comprehend(String, boolean)}
     * with <code>withDefault</code> set to <code>false</code>
     * 
     * @see #comprehend(List)
     * @param resourceNames A list of resource names in String separated by {@link #SEPARATOR}
     *        The effect of calling this method with null <code>resourceName</code> shall be 
     *        equal to calling {@link #comprehend(Collection)} with an empty {@link Collection}
     * @return comprehended resource name list 
     */
    List<String> comprehend(String resourceNames);
    
    /**
     * Comprehend a resource name list in String separated by {@link #SEPARATOR}.
     * 
     * @see #comprehend(List, boolean)
     * @param resourceNames A collection of resource names in String separated by {@link #SEPARATOR}
     *        The effect of calling this method with null <code>resourceName</code> shall be 
     *        equal to calling {@link #comprehend(Collection, boolean)} with an empty {@link Collection}
     * @return comprehended resource name list 
     */
    List<String> comprehend(String resourceNames, boolean withDefault);
    
    /**
     * Return the {@link #DEFAULT} list.
     * 
     * Calling this method equals to calling {@link #comprehend(List, boolean)}
     * with an empty list and <code>withDefault</code> set to true
     * 
     * @return
     */
    List<String> comprehend();
    
    /**
     * Add a dependence relationship. A dependence relationship is
     * presented by a <code>dependent</code> resource and a set of 
     * other resources the <code>dependent</code> depends on.
     * 
     * A <code>dependent</code> resource might not necessarily be a
     * real resource. It could be a pseudo resource called bundle
     * which means once it's referenced, then all it's depends on
     * resources will be placed inline. The resources in the depends
     * on list might be pseudo itself given that it's depends on 
     * will finally interpreted to real resources.
     * 
     * Note <code>IDependencyManager</code> itself does not distinguish
     * real resources from pseudo resources (bundles). It's up to
     * the user program to decide how to manipulate the resources (by
     * name)
     * 
     * @param dependant the name of the resource that depends other resources
     * @param dependsOn the list of resource names the dependent depends on
     *        if duplicate names found in the list, then they are considered
     *        to be the same resource
     * @throws NullPointerException if either dependent or dependsOn is null
     */
    void addDependency(String dependent, Collection<String> dependsOn);
}
