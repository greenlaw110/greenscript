package com.greenlaw110.rythm.play.parsers;

import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.IParserFactory;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.utils.TextBuilder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This parser strip groovy template's #{vertatim} and #{/verbatim} tag. The aim is to allow
 * play's default groovy compiler able to compile rythm template which is included inside #{vertatim} tag
 */
public class GroovyVerbatimTagParser implements IParserFactory {
    private static final Pattern P = Pattern.compile("(#\\{\\/?verbatim\\}\\s*\\r*\\n*).*", Pattern.DOTALL);
    @Override
    public IParser create(IContext ctx) {
        return new ParserBase(ctx) {
            @Override
            public TextBuilder go() {
                Matcher m = P.matcher(remain());
                if (m.matches()) {
                    step(m.group(1).length());
                    return new Token("", ctx());
                } else {
                    return null;
                }
            }
        };
    }

    public static void main(String[] args) {
        String s = "#{/verbatim}";
        Matcher m = P.matcher(s);
        if (m.matches()) {
            System.out.println(m.group(1));
        }
    }
}
