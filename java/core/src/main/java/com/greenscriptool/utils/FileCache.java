package com.greenscriptool.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FileCache implement 
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0, 2010-10-13
 * @since 1.0
 */
public class FileCache {
    
    private File r_;
    
    public FileCache(File root) {
        r_ = root;
    }
    
    Map<List<String>, String> m_ = new HashMap<List<String>, String>();
    
    private File f_(String fn) {
        return new File(r_, fn);
    }
    
    public File createTempFile(String extension) {
        try {
            if (!r_.isDirectory() && !r_.mkdir()) {
              throw new RuntimeException("cannot create temporary directory for: " + r_);
            }
            return File.createTempFile("gstmp", extension, r_);
        } catch (IOException e) {
            String msg = "Error create temp file";
            throw new RuntimeException(msg, e);
        }
    }
    
    /**
     * Return cached filename. This method guarantees that
     * file always exists if a non-null value returned 
     * 
     * @param key
     * @return filename by key if file exists, null otherwise
     */
    public String get(List<String> key) {
        String fn = m_.get(key);
        if (null == fn) return null;
        if (!f_(fn).exists()) {
            m_.remove(key);
            return null;
        }
        return fn;
    }
    
    public String put(List<String> key, String fileName) {
        String old = remove(key);
        m_.put(key, fileName);
        return old;
    }
    
    public String remove(List<String> key) {
        String fn = m_.remove(key);
        if (null == fn) return null;
        delFile_(fn);
        return fn;
    }
    
    /**
     * Clear cache and corresponding files
     */
    public void clear() {
        for (String fn: m_.values()) {
            delFile_(fn);
        }
        m_.clear();
    }
    
    private void delFile_(String fn) {
        File f = f_(fn);
        if (f.exists()) {
            if (!f.delete()) f.deleteOnExit();
        }
    }
        
}
