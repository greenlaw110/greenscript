package com.greenscriptool;


import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;

public abstract class BaseTest extends Assert {
    
    protected File rootDir() {
        File curDir;
        URL url = getClass().getResource(".");
        try {
            curDir = new File(url.toURI());
        } catch (URISyntaxException e) {
            curDir = new File(url.getPath());
        }
        return new File(curDir.getParentFile().getParentFile(), "public");
    }
    /**
     * Return current dir of this class
     */
    protected String root() {
        return rootDir().getAbsolutePath();
    }
    
    protected String str(List<String> names) {
        if (names.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append(names.get(0));
        for (int i = 1; i < names.size(); ++i) {
            sb.append(",").append(names.get(i));
        }
        return sb.toString();
    }
    
    protected void eq(String s, List<String> names) {
        assertEquals(s, str(names));
    }
    
    protected void echo(Object o) {
        System.out.println(o);
    }
}
