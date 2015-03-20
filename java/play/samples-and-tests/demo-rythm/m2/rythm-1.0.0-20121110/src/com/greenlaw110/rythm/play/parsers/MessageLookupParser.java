package com.greenlaw110.rythm.play.parsers;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.CodeToken;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.build_in.KeywordParserFactory;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IKeyword;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 29/01/12
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class MessageLookupParser extends KeywordParserFactory {

    @Override
    public IKeyword keyword() {
        return PlayRythmKeyword._M;
    }

    @Override
    protected String patternStr() {
        return "^(%s%s[\\t ]*((?@())))";
    }

    protected String innerPattern() {
        return "(((?@\"\")|(?@())|(?@'')|[a-zA-Z_][\\w$_\\.]*)(?@())?)(.*)";
    }

    @Override
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) return null;
                String s = r.stringMatched();
                step(s.length());
                s = r.stringMatched(3);
                //strip off ( and )
                s = S.stripBrace(s);
                // now parse message string and parameters
                r = new Regex(innerPattern());
                if (r.search(s)) {
                    String msgStr = r.stringMatched(1);
                    boolean hasQuotation = msgStr.startsWith("'") || msgStr.startsWith("\"");
                    msgStr = S.stripQuotation(msgStr);
                    String param = r.stringMatched(3);
                    if (S.isEmpty(param)) {
                        String fmt = hasQuotation ? "Messages.get(\"%s\")" : "Messages.get(%s)";
                        s = String.format(fmt, msgStr);
                    } else {
                        String fmt = hasQuotation ? "Messages.get(\"%s\" %s)" : "Messages.get(%s %s)";
                        s = String.format(fmt, msgStr, param);
                    }
                    ctx().getCodeBuilder().addImport("play.i18n.Messages");
                    return new CodeToken(s, ctx()){
                        @Override
                        public void output() {
                            p("p(").p(s).p(");");
                            pline();
                        }
                    };
                } else {
                    throw new ParseException(ctx().getTemplateClass(), ctx().currentLine(), "Error parsing message lookup");
                }
            }
        };
    }

    public static void main(String[] args) {

        MessageLookupParser p = new MessageLookupParser();
        Regex r = p.reg(new Rythm());
        String s = "@msg(x, \"rythm\")";
        if (r.search(s)) {
            System.out.println(r.stringMatched());
            s = (r.stringMatched(3));
            System.out.println(">>" + s);
            s = s.substring(1).substring(0, s.length() - 2);
            System.out.println("<<" + s);
//            s = s.substring(0, s.length() - 1);
//            System.out.println(">>" + s);
            //System.out.println(s);
            r = new Regex(p.innerPattern());
            if (r.search(s)) {
                System.out.println("1>>" + r.stringMatched(1));
                System.out.println("2>>" + r.stringMatched(2));
                System.out.println("3>>" + r.stringMatched(3));
                System.out.println("4>>" + r.stringMatched(4));
            }
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
