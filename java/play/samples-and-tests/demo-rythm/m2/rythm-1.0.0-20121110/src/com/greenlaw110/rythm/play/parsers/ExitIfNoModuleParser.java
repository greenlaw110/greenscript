package com.greenlaw110.rythm.play.parsers;

import com.greenlaw110.rythm.exception.ParseException;
import com.greenlaw110.rythm.internal.Keyword;
import com.greenlaw110.rythm.internal.TemplateParser;
import com.greenlaw110.rythm.internal.dialect.Rythm;
import com.greenlaw110.rythm.internal.parser.ParserBase;
import com.greenlaw110.rythm.internal.parser.build_in.KeywordParserFactory;
import com.greenlaw110.rythm.logger.Logger;
import com.greenlaw110.rythm.spi.IContext;
import com.greenlaw110.rythm.spi.IParser;
import com.greenlaw110.rythm.spi.Token;
import com.greenlaw110.rythm.utils.S;
import com.greenlaw110.rythm.utils.TextBuilder;
import com.stevesoft.pat.Regex;
import play.Play;
import play.mvc.Router;
import play.vfs.VirtualFile;

public class ExitIfNoModuleParser extends KeywordParserFactory {

    @Override
    public PlayRythmKeyword keyword() {
        return PlayRythmKeyword.EXIT_IF_NO_MODULE;
    }

    public IParser create(final IContext ctx) {
        return new ParserBase(ctx) {
            public TextBuilder go() {
                Regex r = reg(dialect());
                if (!r.search(remain())) {
                    raiseParseException("error parsing @__exitIfNoPlayModule__, correct usage: @__exitIfNoPlayModule__(\"play-module-name\"");
                }
                step(r.stringMatched().length());
                String s = r.stringMatched(1);
                s = S.stripBraceAndQuotation(s);
                if (Play.modules.containsKey(s)) {
                    return new Token("", ctx());
                } else {
                    throw new TemplateParser.ExitInstruction();
                }
            }
        };
    }

    @Override
    protected String patternStr() {
        return "%s%s\\s*((?@()))[\\s]+";
    }

    public static void main(String[] args) {
        String s = "@__exitIfNoPlayModule__(rythm)\nabc";
        ExitIfNoModuleParser ap = new ExitIfNoModuleParser();
        Regex r = ap.reg(new Rythm());
        p(s, r);
    }
}
