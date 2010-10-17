package com.greenscriptool;


import java.io.File;
import java.util.List;

import org.junit.Assert;

public abstract class BaseTest extends Assert {
    /**
     * Return current dir of this class
     */
    protected String root() {
        File f = new File(".");
        return f.getAbsolutePath();
    }
    
    protected String str(List<String> names) {
        if (names.size() == 0) return "";
        StringBuffer sb = new StringBuffer();
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
