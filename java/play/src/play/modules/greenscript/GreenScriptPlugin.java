package play.modules.greenscript;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import play.Logger;
import play.Play;
import play.Play.Mode;
import play.PlayPlugin;
import play.exceptions.UnexpectedException;
import play.mvc.Http.Request;
import play.mvc.Http.Response;
import play.vfs.VirtualFile;

import com.greenscriptool.DependenceManager;
import com.greenscriptool.IDependenceManager;
import com.greenscriptool.IFileLocator;
import com.greenscriptool.IRenderSession;
import com.greenscriptool.Minimizer;
import com.greenscriptool.RenderSession;
import com.greenscriptool.ResourceType;
import com.greenscriptool.utils.ICompressor;
import com.greenscriptool.utils.YUICompressor;

/**
 * Define a Playframework plugin
 * 
 * @author greenlaw110@gmail.com
 * @version 1.2.1, 2011-01-20 
 *           1. support reverse dependency declaration, e.g:
 *           * js.jquery-1.4.4-=jquery-ui.1.8.7,jquery.tmpl
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
        defProps_.put("greenscript.dir.js", "javascripts");
        defProps_.put("greenscript.dir.css", "stylesheets");
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
    }
    
    @Override
    public void onConfigurationRead() {
        
        loadDependencies();
        InitializeMinimizers();
        
        Logger.info("GreenScript-v1.2d initialized");
    }
	
    @Override
    public void onApplicationStop() {
        cleanUp_();
    }
    
    public String jsDebugString() {
        return ((DependenceManager)jsD_).debugString();
    }
    
    public String cssDebugString() {
        return ((DependenceManager)cssD_).debugString();
    }
    
    private static ThreadLocal<IRenderSession> sessJs_ = new ThreadLocal<IRenderSession>();
    private static ThreadLocal<IRenderSession> sessCss_ = new ThreadLocal<IRenderSession>();
    
    public static IRenderSession session(String type) {
        ResourceType rt = ResourceType.valueOf(type.toUpperCase());
        switch (rt) {
        case JS: return jsSession();
        case CSS: return cssSession();
        }
        throw new UnexpectedException("unknown resource type: " + rt.name());
    }
    
    public static IRenderSession jsSession() {
        return sessJs_.get();
    }
    
    public static IRenderSession cssSession() {
        return sessCss_.get();
    }

    @Override
    public void beforeActionInvocation(Method actionMethod) {
        sessJs_.set(newSession_(ResourceType.JS));
        sessCss_.set(newSession_(ResourceType.CSS));
    }
    
    
    private static YUICompressor jsC_ = new YUICompressor(ResourceType.JS);
    private static YUICompressor cssC_ = new YUICompressor(ResourceType.CSS);
    @Override
    public boolean serveStatic(VirtualFile file, Request request, Response response) {
        String fn = file.getName();
        if (jsM_.isMinimizeEnabled() && fn.endsWith(".js")) {
            return compressStatic_(file, response, ResourceType.JS);
        }
        if (cssM_.isMinimizeEnabled() && fn.endsWith("css")) {
            return compressStatic_(file, response, ResourceType.CSS);
        }
        
        return super.serveStatic(file, request, response);
    }
    
    private boolean compressStatic_(VirtualFile file, Response resp, ResourceType type) {
        IRenderSession sess = type == ResourceType.JS ? jsSession() : cssSession();
        if (null != sess && sess.hasDeclared()) {
            return false;
        }
        if (Play.mode == Mode.PROD) {
            resp.cacheFor("1h");
        }
        ICompressor comp = type == ResourceType.JS ? jsC_ : cssC_;
        try {
            StringWriter w = new StringWriter();
            comp.compress(file.getRealFile(), w);
            resp.contentType = type == ResourceType.JS ? "text/javascript" : "text/css";
            resp.status = 200;
            resp.print(w);
            return true;
        } catch (Exception e) {
            Logger.warn(e, "error compress file %1$s", file.getName());
            return false;
        }
    }
    
    public void loadDependencies() {
        Properties p = new Properties();
        for (VirtualFile vf: Play.roots) {
        	VirtualFile conf = vf.child("conf/greenscript.conf");
        	if (conf.exists()) {
                Logger.trace("loading dependency configuration from %1$s", conf.getRealFile().getAbsolutePath());
        		try {
        			p.load(new BufferedInputStream(conf.inputstream()));
        		} catch (Exception e) {
        			throw new UnexpectedException("error loading conf/greenscript.conf");
        		}
        	}
        }
        jsD_ = new DependenceManager(loadDepProp_(p, "js"));
        cssD_ = new DependenceManager(loadDepProp_(p, "css"));
        
        depConf_ = p;
    }
    
    public void InitializeMinimizers() {
        Properties p = Play.configuration;
        
        for (String key: p.stringPropertyNames()) {
            if (key.startsWith("greenscript.")) {
                String v = p.getProperty(key);
                minConf_.setProperty(key, p.getProperty(key));
                Logger.trace("[greenscript]set %1$s to %2$s", v, key);
            }
        }

        jsM_ = initializeMinimizer_(minConf_, ResourceType.JS);
        cssM_ = initializeMinimizer_(minConf_, ResourceType.CSS);
        
        if (p.containsKey("greenscript.useGoogleClosure")) {
        	System.setProperty("greenscript.useGoogleClosure", p.getProperty("greenscript.useGoogleClosure"));
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
	
	private String join_(Collection<String> c) {
		boolean first = true;
		StringBuffer sb = new StringBuffer();
		for (String s: c) {
			if (!first) sb.append(",");
			else first = false;
			sb.append(s);
		}
		return sb.toString();
	}
	
	private void mergeProperties_(Properties p, String k, String v){
      String oldV = p.getProperty(k);
      if (null == oldV) {
          p.setProperty(k, v);
      } else {
          Set<String> oldS = new HashSet<String>();
          oldS.addAll(Arrays.asList(oldV.split(IDependenceManager.SEPARATOR)));
          Set<String> newS = new HashSet<String>();
          newS.addAll(Arrays.asList(v.split(IDependenceManager.SEPARATOR)));
          oldS.addAll(newS);
          p.setProperty(k, join_(oldS));
      }
	}
	
    // type should be "js" or "css"
    private Properties loadDepProp_(Properties p, String type) {
        Properties p0 = new Properties();
        String prefix = type + ".";
        for (String k: p.stringPropertyNames()) {
            if (k.startsWith(prefix)) {
                String k0 = k.replace(prefix, "");
                String v = p.getProperty(k);
                if (k0.matches(".*\\s*\\-\\s*$")) {
                    // reverse dependency declaration
                    k0 = k0.replaceAll("\\s*\\-\\s*$", "");
                    for (String s: v.replaceAll("\\s+", "").split(IDependenceManager.SEPARATOR)) {
                        mergeProperties_(p0, s, k0);
                    }
                } else {
                    mergeProperties_(p0, k0, v);
                }
                Logger.trace("Found one %1$s dependency: %2$s depends on '%3$s'", type, k0, v);
            }
        }
        return p0;
    }
    
    private Minimizer initializeMinimizer_(Properties p, ResourceType type) {
        Minimizer m = new Minimizer(type);
        String ext = type.getExtension();
        String rootDir = fetchProp_(p, "greenscript.dir.root");
        String resourceDir = fetchProp_(p, "greenscript.dir" + ext);
        String cacheDir = fetchProp_(p, "greenscript.dir.minimized");
        
        String resourceUrl = fetchProp_(p, "greenscript.url" + ext);
        String cacheUrl = fetchProp_(p, "greenscript.url.minimized");
        
        m.setCacheDir(Play.getFile(cacheDir));
        m.setCacheUrlPath(cacheUrl);
        m.setResourceDir(resourceDir);
        m.setResourceUrlPath(resourceUrl);
        m.setRootDir(rootDir);
        
        boolean minimize = getBooleanProp_(p, "greenscript.minimize", Play.mode == Mode.PROD);
        boolean compress = getBooleanProp_(p, "greenscript.compress", true);
        boolean cache = getBooleanProp_(p, "greenscript.cache", true);
        
        m.enableDisableMinimize(minimize);
        m.enableDisableCompress(compress);
        m.enableDisableCache(cache);
        m.setFileLocator(new IFileLocator(){
        	public File locate(String path) {
        		VirtualFile vf = VirtualFile.search(Play.roots, path);
        		return vf == null ? null : vf.getRealFile();
        	}
        });
        
        Logger.trace("minimizer for %1$s loaded", type.name());
        return m;
    }
    
    private String fetchProp_(Properties p, String key) {
        String val = p.getProperty(key);
        if (null == val) val = defProps_.getProperty(key);
        return val;
    }
    
    private boolean getBooleanProp_(Properties p, String key, boolean def) {
        try {
            String s = p.containsKey(key) ? p.getProperty(key) 
                    : defProps_.containsKey(key) ? defProps_.getProperty(key)
                            : String.valueOf(def);

            p.setProperty(key, s);
            return Boolean.parseBoolean(s);
        } catch (Exception e) {
            p.setProperty(key, String.valueOf(def));
            return def;
        }
    }
    
//    private File getDir_(String dir) {
//        return Play.getFile(dir);
//    }
    
    private void cleanUp_() {
        if (null != jsM_) jsM_.clearCache();
        if (null != cssM_) cssM_.clearCache();
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
