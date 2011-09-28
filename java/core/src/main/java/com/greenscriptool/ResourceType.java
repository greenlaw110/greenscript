package com.greenscriptool;

import java.util.Arrays;
import java.util.List;

/**
 * Resource type enumeration 
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0, 2010-10-13
 * @since 1.0
 */
public enum ResourceType {
    JS(".js", ".coffee"), CSS(".css", ".less");
    private ResourceType(String... extension) {
        if (extension.length == 0) throw new IllegalArgumentException("needs at least one extension"); 
        exts_ = extension;
    }
    private String[] exts_;
    public String getExtension() {
        return exts_[0];
    }
    public List<String> getAllExtensions() {
        return Arrays.asList(exts_);
    }
}
