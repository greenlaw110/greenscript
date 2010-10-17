package com.greenscriptool;

/**
 * Resource type enumeration 
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0, 2010-10-13
 * @since 1.0
 */
public enum ResourceType {
    JS(".js"), CSS(".css");        
    private ResourceType(String extension) {
        if (null == extension) throw new NullPointerException("extension cannot be null");
        ext_ = extension;
    }
    private String ext_;
    public String getExtension() {
        return ext_;
    }
}
