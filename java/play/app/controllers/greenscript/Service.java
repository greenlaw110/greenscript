package controllers.greenscript;

import play.modules.greenscript.GreenScriptPlugin;
import play.mvc.Controller;

public class Service extends Controller {
    
    public static void getInMemoryCache(String key) {
        String content = GreenScriptPlugin.getInstance().getInMemoryFileContent(key);
        notFoundIfNull(content);
        response.cacheFor("1d");
        if (key.endsWith(".js")) {
            response.setContentTypeIfNotSet("text/javascript");
        } else if (key.endsWith(".css")) {
            response.setContentTypeIfNotSet("text/css");
        }
        
        renderText(content);
    }

}
