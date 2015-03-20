package com.greenlaw110.rythm.play.parsers;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.compiler.TemplateClass;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.build_in.KeywordParserFactory;
import com.greenlaw110.rythm.play.utils.StaticRouteResolver;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IKeyword;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;
import play.Play;
import play.mvc.Router;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 29/01/12
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class UrlReverseLookupParser extends KeywordParserFactory {

    protected boolean isAbsolute = false;

    @Override
    public IKeyword keyword() {
        return PlayRythmKeyword._U;
    }

    @Override
    protected String patternStr() {
        return "^(%s%s[ \\t]*((?@())))";
    }

    protected String innerPattern() {
        return "[a-zA-Z_][\\w$_\\.]*(?@())?";
    }

    private static final ConcurrentMap<String, String> staticRouteMap = new ConcurrentHashMap<String, String>();
    private static final ConcurrentMap<String, String> staticAbsoluteRouteMap = new ConcurrentHashMap<String, String>();

    @Override
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
//                    if (isAbsolute) {
//                        throw new ParseException(ctx().getTemplateClass(), ctx().currentLine(), "Error parsing @fullUrl statement, correct usage: @fullUrl(Controller.action), or @fullUrl(/public/<your public assets>)");
//                    } else {
//                        throw new ParseException(ctx().getTemplateClass(), ctx().currentLine(), "Error parsing @url statement, correct usage: @url(Controller.action), or @url(/public/<your public assets>)");
//                    }
                    // allow @url expression
                    return null;
                }
                String s = r.stringMatched();
                step(s.length());
                s = r.stringMatched(3);
                s = S.stripBraceAndQuotation(s);
                if (S.isEmpty(s)) {
                    if (isAbsolute) {
                        raiseParseException("Error parsing @fullUrl() tag, controller action or static assets expected");
                    } else {
                        raiseParseException("Error parsing @url() tag, controller action or static assets expected");
                    }
                }
                // try to see if it is a static url
                Map<String, String> routeMap = isAbsolute ? staticAbsoluteRouteMap : staticRouteMap;
                String staticUrl = routeMap.get(s);
                if (null == staticUrl) {
                    try {
                        staticUrl = StaticRouteResolver.reverseWithCheck(s, isAbsolute);
                    } catch (play.exceptions.NoRouteFoundException e) {
                    }
                }
                if (null != staticUrl) {
                    routeMap.put(s, staticUrl);
                    return new Token(staticUrl, ctx());
                } else {
                    if (s.startsWith("/") || s.startsWith("\\")) {
                        raiseParseException("Static URL lookup failed: %s", s);
                    }
                    // otherwise ignore it and try controller action
                }

                // now try parse action name and params
                r = new Regex("([a-zA-Z_][\\w$_\\.]*)((?@())?)");
                if (r.search(s)) {
                    final String action = r.stringMatched(1);
                    s = r.substring(2);
                    //strip off ( and ) if there is
                    if (null == s) s = "";
                    if (s.startsWith("(")) {
                        s = s.substring(1);
                    }
                    if (s.endsWith(")")) {
                        s = s.substring(0, s.length() - 1);
                    }

                    TemplateClass tc = ctx().getTemplateClass();
                    boolean escapeXML = (!tc.isStringTemplate() && tc.templateResource.getKey().toString().endsWith(".xml"));

                    s = new TextBuilder().p("p(s().raw(new com.greenlaw110.rythm.play.utils.ActionBridge(").p(isAbsolute).p(",").p(escapeXML).p(").invokeMethod(\"").p(action).p("\", new Object[] {").p(s).p("})));").toString();
                    return new CodeToken(s, ctx());
                } else {
                    throw new ParseException(ctx().getTemplateClass(), ctx().currentLine(), "Error parsing url reverse lookup");
                }
            }
        };
    }

    public static void main(String[] args) {
        UrlReverseLookupParser p = new UrlReverseLookupParser();
        Regex r = p.reg(new Rythm());
        String s = "@url() abc";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            s = (r.stringMatched(3));
            System.out.println(">>" + s);
            s = s.substring(1);
            s = s.substring(0, s.length() - 1);
            System.out.println("<<" + s);
//            s = s.substring(0, s.length() - 1);
//            System.out.println(">>" + s);
            if (s.startsWith("\"") || s.startsWith("'")) {
                s = s.substring(1);
            }
            if (s.endsWith("\"") || s.endsWith("'")) {
                s = s.substring(0, s.length() - 1);
            }
            System.out.println(s);
        }

//        s = "RythmTester.test(a.boc(), 14, '3', \"aa\")";
//        //s = "getId()";
//        r = new Regex("([a-zA-Z_][\\w$_\\.]*)((?@())?)");
//        if (r.search(s)) {
//            System.out.println(r.stringMatched());
//            System.out.println(r.stringMatched(1));
//            System.out.println(r.stringMatched(2));
//            System.out.println(r.stringMatched(3));
//
//            s = r.substring(2);
//            //strip off ( and ) if there is
//            if (null == s) s = "";
//            if (s.startsWith("(")) {
//                s = s.substring(1);
//            }
//            if (s.endsWith(")")) {
//                s = s.substring(0, s.length() - 1);
//            }
//            System.out.println(s);
//        }
    }
}
