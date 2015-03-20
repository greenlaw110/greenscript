package com.greenlaw110.rythm.play.parsers;

import com.greenlaw110.rythm.spi.IKeyword;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 29/01/12
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbsoluteUrlReverseLookupParser extends UrlReverseLookupParser {
    public AbsoluteUrlReverseLookupParser() {
        this.isAbsolute = true;
    }

    @Override
    public IKeyword keyword() {
        return PlayRythmKeyword._AU;
    }
}
