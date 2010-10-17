package controllers.greenscript;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import play.Play;
import play.modules.greenscript.GreenScriptPlugin;
import play.mvc.Controller;

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
        render(cssDeps, jsDeps, minConf);
    }

    public static void update(boolean minimize, boolean compress, boolean cache) {
        GreenScriptPlugin.updateMinimizer(minimize, compress, cache);
        
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
