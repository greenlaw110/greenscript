package com.greenscriptool;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MinimizerTest extends BaseTest {
    protected Minimizer jm;
    protected Minimizer cm;
    
    
    protected File rootDir;
    protected File jsDir;
    protected File cssDir;
    protected File cacheDir;
    
    protected String jsUrlPath = "/js";
    protected String cssUrlPath = "/css";
    protected String cacheUrlPath = "/gs";
    
    protected List<String> l = null; // temporarily holding processing result
    
    @Before
    public void setUp() {
        rootDir = rootDir();
        cssDir = new File(rootDir, "stylesheets");
        jsDir = new File(rootDir, "javascripts");
        cacheDir = new File(rootDir, "gsCache");
        
        //echo (new File(rootDir).getAbsolutePath());
        jm = new Minimizer(ResourceType.JS);
        jm.setRootDir(rootDir.getAbsolutePath());
        jm.setResourceDir("javascripts");
        jm.setCacheDir(cacheDir);
        jm.setResourceUrlPath(jsUrlPath);
        jm.setCacheUrlPath(cacheUrlPath);
        
        cm = new Minimizer(ResourceType.CSS);
        cm.setRootDir(rootDir.getAbsolutePath());
        cm.setResourceDir("stylesheets");
        cm.setCacheDir(cacheDir);
        cm.setResourceUrlPath(cssUrlPath);
        cm.setCacheUrlPath(cacheUrlPath);
        
        jm.enableDisableMinimize(false);
        cm.enableDisableMinimize(false);
    }
    
    @After
    public void tearDown() {
        jm.clearCache();
        cm.clearCache();
    }
    
    @Test
    public void testSetup () {
        // if no exception then passed
    }
    
    @Test(expected = IllegalStateException.class)
    public void testInvalidSetup() {
        jm.setCacheDir(cacheDir);
    }
    
    @Test
    public void testProcessWithMinimizeDisabled() {
        // normal js case
        v_("/js/a.js,/js/b.js,/c.js", "a,b,/c", jm);
        // with cdn
        v_("/js/a.js,http://abc.com/a.js,/js/b.js", "a,http://abc.com/a.js,/js/b.js", jm);
        
        // normal css case
        v_("/css/b.css,/f1/c.css,/css/a.css", "b,/f1/c.css,a", cm);
        
        // _bundle convention
        v_("/js/a.js", "a,abc.bundle", jm);
        
        // bad resource when verifyResource is disabled
        v_("/js/a.js,/js/faked.js,/c.js", "a,faked,/c", jm);
        
        // bad resource when verifyResource is enabled
        cm.enableDisableVerifyResource(true);
        v_("/css/a.css,/f1/c.css", "a,faked,/f1/c", cm);
        
    }
    
    @Test
    public void testProcessWithMinimizeEnabled() {
        // normal js case
        jm.enableDisableMinimize(true);
        p_("a,b,/c",jm);
        assertSame(1, l.size());
        assertTrue(l.get(0).startsWith(cacheUrlPath));
        
        // with CDN
        p_("a,http://abc.com/a.js,b,/c", jm);
        assertSame(2, l.size());
        assertTrue(l.get(1).startsWith(cacheUrlPath));
        assertEquals("http://abc.com/a.js", l.get(0));
        
        // cache is not enabled, so the 2 processes on same name list return different file name
        p_("a,b", jm);
        String s0 = l.get(0);
        p_("a,b", jm);
        String s1 = l.get(0);
        assertFalse(s0.equals(s1));
        
        // enable cache and see again
        jm.enableDisableCache(true);
        p_("a,b", jm);
        s0 = l.get(0);
        p_("a,b", jm);
        s1 = l.get(0);
        assertTrue(s0.equals(s1));
        
        // bad file shall not cause exception
        p_("a,b,faked", jm);
    }
    
    private void v_(String expected, String names, IMinimizer m) {
        p_(names, m);
        eq(expected, l);
    }
    
    private void p_(String names, IMinimizer m) {
        l = m.process(Arrays.asList(names.split(IDependenceManager.SEPARATOR)));
    }
    
}
