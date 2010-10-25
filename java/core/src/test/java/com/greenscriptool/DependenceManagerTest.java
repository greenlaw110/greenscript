package com.greenscriptool;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.junit.Test;

public class DependenceManagerTest extends BaseTest {
    
    protected IDependenceManager dm = null;
    
    private Properties load_(String fileName) throws IOException{
        InputStream is = getClass().getResourceAsStream(fileName);
        Properties p = new Properties();
        p.load(is);
        return p;
    }
    
    private DependenceManager getDM_(String fileName) throws IOException {
        return new DependenceManager(load_(fileName));
    }
    
    private void l_(String fileName) throws IOException {
        dm = getDM_(fileName);
    }
        
    private void v_(String s, String t) {
        List<String> l = dm.comprehend(t);
        eq(s, l);
    }
    
    private void v_(String s, String t, boolean withDefault) {
        List<String> l = dm.comprehend(t, withDefault);
        eq(s, l);
    }
    
    /*
     * a=b,
     */
    @Test
    public void testSimpleDependencies() throws Exception {
        l_("simple.properties");
        v_("b,a", "a,b");
        v_("b,a", "a");
        v_("b", "b");
        v_("b,a,y,x,z", "y,a,x, ,,y,z");
        v_("x,y,z", "x,y,x ,z y x");
    }
    
    /*
     * a=b
     * b=c
     */
    @Test
    public void testIndirectDependencies() throws Exception {
        l_("indirect.properties");
        
        v_("c,b,a", "a");
        v_("c,b", "b");
        v_("c", "c");
        v_("c,b", "b,c");
    }
    
    /*
     * a=b,c,d,
     * c=b,x,y
     * d=o,p ,x,
     * e=i,j,k
     * x=y,z
     * p=z
     * o=z
     */
    @Test
    public void testComplexDependencies() throws Exception {
        l_("complex.properties");
        v_("z,y,x,p,o,b,d,c,a", "a");
        v_("k,j,i,e", "e");
        v_("z,y,x,p,o,k,j,i,d,e", "p,e,d");
    }
    
    /*
     * default=x,y,b
     * a=b
     * b=c
     */
    @Test
    public void testDefaultDependencies() throws Exception {
        l_("default.properties");
        v_("c,b", "b", false);
        v_("c,b,y,x,default", "b", true);
        v_("c,b,y,x,default,a", "a", true);
    }
    
    /*
     * a=b,x,y
     * b=c,i,j
     * c=i,j,k
     * k=a,z
     */
    @Test(expected=CircularDependenceException.class)
    public void testCircularDependencies() throws Exception {
       l_("circular.properties");
    }
}
