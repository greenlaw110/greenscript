package com.greenscriptool.utils;

import com.greenscriptool.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.Reader;
import java.io.Writer;

/**
 * A simple JS compressor just remove redundant spaces and lines
 */
public class SimpleJSCompressor implements ICompressor {
    private static Log logger_ = LogFactory.getLog(ICompressor.class);
    private ResourceType type_;

    public SimpleJSCompressor(ResourceType type) {
        if (ResourceType.JS != type)
            throw new IllegalArgumentException("UglifyJS compressor does not support CSS compression");
        type_ = type;
    }

    @Override
    public void compress(Reader r, Writer w) throws Exception {
        int blankCount = 0;
        int lineCount = 0;
        int c = r.read();
        while (c != -1) {
            String s = String.valueOf((char)c);
            if (s.matches("[ \t]")) {
                blankCount++;
                if (blankCount <= 1) {
                    w.write(c);
                }
            } else if (s.matches("[\\r\\n]")) {
                lineCount++;
                if (lineCount <= 1) {
                    w.write(c);
                }
            } else {
                blankCount = 0;
                lineCount = 0;
                w.write(c);
            }
            c = r.read();
        }
    }

    public static void main(String[] args) throws Exception {
        new SimpleJSCompressor(ResourceType.JS).compress(null, null);
    }
}
