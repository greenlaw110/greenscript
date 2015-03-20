package com.greenlaw110.rythm.play.utils;

import com.greenlaw110.rythm.spi.IExpressionProcessor;
import com.greenlaw110.rythm.spi.Token;

/**
 * Created with IntelliJ IDEA.
 * User: luog
 * Date: 28/03/12
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionInvokeProcessor implements IExpressionProcessor {
    @Override
    public boolean process(String exp, Token token) {
        String s = exp;
        if (s.indexOf("(") > 0 && s.startsWith("controllers.")) {
            String action = s.replaceFirst("controllers.", "");
            int pos = action.indexOf("(");
            action = action.substring(0, pos);
            token.p("com.greenlaw110.rythm.play.RythmPlugin.setActionCallFlag();play.mvc.Http.Request.current().action=\"").p(action).p("\";\ntry{").p(s).p(";} catch (RuntimeException e) {handleTemplateExecutionException(e);}");
            token.pline();
            return true;
        }
        return false;
    }
}
