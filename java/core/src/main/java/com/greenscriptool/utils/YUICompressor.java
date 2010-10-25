package com.greenscriptool.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import com.greenscriptool.ResourceType;
import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

/**
 * Implement {@link ICompressor} using YUI compressor library
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0, 2010-10-16
 * @since 1.0
 */
public class YUICompressor implements ICompressor {
    private static Log logger_ = LogFactory.getLog(ICompressor.class);
    
    private ResourceType type_;
    
    public YUICompressor(ResourceType type) {
        type_ = type;
    }

    @Override
    public void compress(File input, Writer output) throws Exception {
        if (null == input || null == output) throw new NullPointerException();
        if (logger_.isTraceEnabled()) logger_.trace(String.format("compressing %1$s ...", input.getName()));
        Reader r = new BufferedReader(new FileReader(input));
        try {
            switch (type_) {
            case CSS:
                new CssCompressor(r).compress(output, -1);
                break;
            case JS:
                new JavaScriptCompressor(r, er_).compress(output, -1, true, false, false, false);
                break;
            default:
                throw new RuntimeException("Resource type not recognized: " + type_.name());
            }
        } finally {
            if (null != r) {
                try {
                    r.close();
                } catch (IOException e) {
                    logger_.warn("error closing file: " + input.getName(), e);
                }
            }
        }
    }

    private static ErrorReporter er_ = new ErrorReporter() {

        public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                logger_.warn("[MINIMIZOR.WARNING] " + message);
            } else {
                logger_.warn(String.format("[MINIMIZOR.WARNING] %1$s: %2$s: %3$s", line, lineOffset, message));
            }
        }

        public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
            if (line < 0) {
                logger_.error("[MINIMIZOR.ERROR] " + message);
            } else {
                logger_.error(String.format("[MINIMIZOR.ERROR] %1$s: %2$s: %3$s", line, lineOffset, message));
            }
        }

        public EvaluatorException runtimeError(String message, String sourceName, int line,
                String lineSource, int lineOffset) {
            error(message, sourceName, line, lineSource, lineOffset);
            return new EvaluatorException(message);
        }
    };
    
}
