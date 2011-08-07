package controllers.greenscript;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import controllers.Secure;

import play.modules.greenscript.GreenScriptPlugin;
import play.mvc.Controller;
import play.mvc.With;

@With(Secure.class)
public class Configurator extends Controller {

    public static void configure() {
        GreenScriptPlugin gs = GreenScriptPlugin.getInstance();
        
        // js and css dependencies
        List<String> jsDeps = new ArrayList<String>();
        List<String> cssDeps = new ArrayList<String>();
        Properties depConf = gs.getDependencyConfig();
        for (String key: depConf.stringPropertyNames()) {
            if (key.startsWith("js") || key.startsWith("css")) {
                StringBuilder sb = new StringBuilder(key);
                sb.append("=");
                sb.append(depConf.getProperty(key));
                if (key.startsWith("js"))
                    jsDeps.add(sb.toString());
                else
                    cssDeps.add(sb.toString());
            }
        }
        
        // minimizer config
        Properties minConf = gs.getMinimizerConfig();
        
        String jsDebug = gs.jsDebugString();
        String cssDebug = gs.cssDebugString();
        
        render(cssDeps, jsDeps, minConf, jsDebug, cssDebug);
    }

    public static void update(boolean minimize, boolean compress, boolean cache, boolean inMemoryCache) {
        GreenScriptPlugin.updateMinimizer(minimize, compress, cache, inMemoryCache);
        
        flash.success("Setting updated");
        flash.keep();
        configure();
    }
    
    public static void reloadDependencies() {
        GreenScriptPlugin.reloadDependencies();
        flash.success("Dependency configure reloaded");
        flash.now("tab", "#tab-deps");
        flash.keep();
        configure();
    }
}
