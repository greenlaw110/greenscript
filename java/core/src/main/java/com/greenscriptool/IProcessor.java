package com.greenscriptool;

import java.util.List;

/**
 * Defines a general resource processor interface
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0, 2010-10-14
 * @since 1.0
 */
public interface IProcessor {
    
    /**
     * Process a list of resources
     * 
     * @param resourceNames a list of resource names
     * @return processed resource name list
     */
    List<String> process(List<String> resourceNames);

}
