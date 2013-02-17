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
 * Implement {@link ICompressor} using UglifyJS
 * 
 * @see <a href="https://github.com/mishoo/UglifyJS2">https://github.com/mishoo/UglifyJS2</a>
 */
public class UglifyJSCompressor implements ICompressor {
    private static Log logger_ = LogFactory.getLog(ICompressor.class);
    private ResourceType type_;
    ScriptEngineManager sem = new ScriptEngineManager();
    ScriptEngine engine = sem.getEngineByName("JavaScript");

    public UglifyJSCompressor(ResourceType type) {
        if (ResourceType.JS != type)
            throw new IllegalArgumentException("UglifyJS compressor does not support CSS compression");
        type_ = type;
    }

    @Override
    public void compress(Reader r, Writer w) throws Exception {
        String script = "function hello(name) { return ('Hello, ' + name); }";
        engine.eval(script);
        Invocable inv = (Invocable) engine;
        inv.invokeFunction("hello", "Scripting!!" );
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws Exception {
        new UglifyJSCompressor(ResourceType.JS).compress(null, null);
    }
}
