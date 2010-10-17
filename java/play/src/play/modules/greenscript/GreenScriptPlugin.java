package play.modules.greenscript;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Properties;

import play.Logger;
import play.Play;
import play.Play.Mode;
import play.PlayPlugin;
import play.data.validation.MinCheck;
import play.exceptions.UnexpectedException;
import play.mvc.Scope;
import play.mvc.Scope.RenderArgs;

import com.greenscriptool.DependenceManager;
import com.greenscriptool.IDependenceManager;
import com.greenscriptool.IMinimizer;
import com.greenscriptool.IRenderSession;
import com.greenscriptool.Minimizer;
import com.greenscriptool.RenderSession;
import com.greenscriptool.ResourceType;

/**
 * Define a Playframework plugin
 * 
 * @author greenlaw110@gmail.com
 * @version 1.2, 2010-10-16
 */
public class GreenScriptPlugin extends PlayPlugin {

    private Minimizer jsM_;
    private Minimizer cssM_;
    private IDependenceManager jsD_;
    private IDependenceManager cssD_;
    
    private Properties depConf_;
    private Properties minConf_;
    
    private static Properties defProps_; static {
        defProps_ = new Properties();
        // file paths
        defProps_.put("greenscript.dir.root", "/public");
        defProps_.put("greenscript.dir.js", "/public/javascripts");
        defProps_.put("greenscript.dir.css", "/public/stylesheets");
        defProps_.put("greenscript.dir.minimized", "/public/gs");
        // url paths
        defProps_.put("greenscript.url.js", "/public/javascripts");
        defProps_.put("greenscript.url.css", "/public/stylesheets");
        defProps_.put("greenscript.url.minimized", "/public/gs");
        // operation switches
        defProps_.setProperty("greenscript.minimize", Play.mode == Mode.PROD ? "true" : "false");
        defProps_.setProperty("greenscript.compress", "true");
        defProps_.setProperty("greenscript.cache", "true");
    }
    
    public GreenScriptPlugin() {
        //depConf_ = new Properties();
        minConf_ = new Properties();
        minConf_.putAll(defProps_);
        jsM_ = new Minimizer(ResourceType.JS);
        cssM_ = new Minimizer(ResourceType.CSS);
    }
    
    @Override
    public void onConfigurationRead() {
        
        loadDependencies();
        InitializeMinimizers();
        
        Logger.info("GreenScript module initialized");
    }

    @Override
    public void beforeActionInvocation(Method actionMethod) {
        RenderArgs args = Scope.RenderArgs.current();
        args.put("gsJsSession", newSession_(ResourceType.JS));
        args.put("gsCssSession", newSession_(ResourceType.CSS));
    }
    
    public void loadDependencies() {
        Properties p = new Properties();
        File f = Play.getFile("conf/greenscript.conf");
        Logger.trace("loading dependency configuration from %1$s", f.getAbsolutePath());
        if (f.isFile()) {
            try {
                p.load(new BufferedInputStream(new FileInputStream(f)));
            } catch (Exception e) {
                throw new UnexpectedException("error loading conf/greenscript.conf");
            }
            
            jsD_ = new DependenceManager(loadDepProp_(p, "js"));
            cssD_ = new DependenceManager(loadDepProp_(p, "css"));
        } else {
            jsD_ = new DependenceManager(p);
            cssD_ = new DependenceManager(p);
        }
        
        depConf_ = p;
    }
    
    public void InitializeMinimizers() {
        Properties p = Play.configuration;
        
        initializeMinimizer_(jsM_, p);
        initializeMinimizer_(cssM_, p);
        
        for (String key: p.stringPropertyNames()) {
            if (key.startsWith("greenscript.")) {
                String v = p.getProperty(key);
                minConf_.setProperty(key, p.getProperty(key));
                Logger.trace("[greenscript]set %1$s to %2$s", v, key);
            }
        }
    }
    
    private IRenderSession newSession_(ResourceType type) {
        return type == ResourceType.JS ?
                new RenderSession(jsM_, jsD_, type) :
                    new RenderSession(cssM_, cssD_, type);
    }

    public Properties getDependencyConfig() {
        Properties p = new Properties();
        p.putAll(depConf_);
        return p;
    }
    
    public Properties getMinimizerConfig() {
        Properties p = new Properties();
        p.putAll(minConf_);
        return p;
    }
    
    // prefix should be "js" or "css"
    private Properties loadDepProp_(Properties p, String type) {
        Properties p0 = new Properties();
        String prefix = type + ".";
        for (String k: p.stringPropertyNames()) {
            if (k.startsWith(prefix)) {
                String k0 = k.replace(prefix, "");
                String v = p.getProperty(k);
                p0.setProperty(k0, p.getProperty(k));
                Logger.trace("Found one %1$s dependency: %2$s depends on '%3$s'", type, k0, v);
            }
        }
        return p0;
    }
    
    private void initializeMinimizer_(Minimizer m, Properties p) {
        ResourceType type = m.getType();
        String ext = type.getExtension();
        String rootDir = fetchProp_(p, "greenscript.dir.root");
        String resourceDir = fetchProp_(p, "greenscript.dir" + ext);
        String cacheDir = fetchProp_(p, "greenscript.dir.minimized");
        
        String resourceUrl = fetchProp_(p, "greenscript.url" + ext);
        String cacheUrl = fetchProp_(p, "greenscript.url.minimized");
        
        m.setCacheDir(getDir_(cacheDir));
        m.setCacheUrlPath(cacheUrl);
        m.setResourceDir(getDir_(resourceDir));
        m.setResourceUrlPath(resourceUrl);
        m.setRootDir(getDir_(rootDir));
        
        boolean minimize = getBooleanProp_(p, "greenscript.minimize");
        boolean compress = getBooleanProp_(p, "greenscript.compress");
        boolean cache = getBooleanProp_(p, "greenscript.cache");
        
        m.enableDisableMinimize(minimize);
        m.enableDisableCompress(compress);
        m.enableDisableCache(cache);
        
        Logger.trace("minimizer for %1$s loaded", type.name());
    }
    
    private String fetchProp_(Properties p, String key) {
        String val = p.getProperty(key);
        if (null == val) val = defProps_.getProperty(key);
        return val;
    }
    
    private boolean getBooleanProp_(Properties p, String key) {
        try {
            return Boolean.parseBoolean(p.getProperty(key));
        } catch (Exception e) {
            return false;
        }
    }
    
    private File getDir_(String dir) {
        return Play.getFile(dir);
    }
    
    public static GreenScriptPlugin getInstance() {
        for (PlayPlugin pp: Play.plugins) {
            if (pp instanceof GreenScriptPlugin) return (GreenScriptPlugin)pp;
        }
        return null;
    }
    
    public static void updateMinimizer(boolean minimize, boolean compress, boolean cache) {
        GreenScriptPlugin gs = getInstance();
        gs.jsM_.enableDisableMinimize(minimize);
        gs.jsM_.enableDisableCompress(compress);
        gs.jsM_.enableDisableCache(cache);
        
        gs.cssM_.enableDisableMinimize(minimize);
        gs.cssM_.enableDisableCompress(compress);
        gs.cssM_.enableDisableCache(cache);

        gs.minConf_.setProperty("greenscript.minimize", String.valueOf(minimize));
        gs.minConf_.setProperty("greenscript.compress", String.valueOf(compress));
        gs.minConf_.setProperty("greenscript.cache", String.valueOf(cache));
    }

    public static void enableDisableMinimize(boolean minimize) {
        GreenScriptPlugin gs = getInstance();
        gs.jsM_.enableDisableMinimize(minimize);
        gs.minConf_.setProperty("greenscript.minimize", String.valueOf(minimize));
    }

    public static void enableDisableCompress(boolean compress) {
        GreenScriptPlugin gs = getInstance();
        gs.jsM_.enableDisableCompress(compress);
        gs.minConf_.setProperty("greenscript.compress", String.valueOf(compress));
    }

    public static void enableDisableCache(boolean cache) {
        GreenScriptPlugin gs = getInstance();
        gs.jsM_.enableDisableCache(cache);
        gs.minConf_.setProperty("greenscript.cache", String.valueOf(cache));
    }
    
    public static void reloadDependencies() {
        GreenScriptPlugin gs = getInstance();
        gs.loadDependencies();
    }
}
