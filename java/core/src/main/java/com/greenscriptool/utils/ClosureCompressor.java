package com.greenscriptool.utils;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;
import com.greenscriptool.Minimizer;
import com.greenscriptool.ResourceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by IntelliJ IDEA.
 * User: luog
 * Date: 16/02/12
 * Time: 12:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class ClosureCompressor implements ICompressor {

    private static Log logger_ = LogFactory.getLog(ICompressor.class);
    CompilerOptions options = new CompilerOptions();
    private ResourceType type_;
    private List<JSSourceFile> externalJavascriptFiles = new ArrayList<JSSourceFile>();

    public ClosureCompressor(ResourceType type) {
        if (ResourceType.JS != type) throw new IllegalArgumentException("ClosureCompressor does not support CSS compression");
        type_ = type;
        com.google.javascript.jscomp.Compiler.setLoggingLevel(Level.FINE);
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
    }

    @Override
    public void compress(Reader r, Writer w) throws Exception {
        com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();
        JSSourceFile file = JSSourceFile.fromInputStream("greenscript.js", new ReaderInputStream(r));
        List<JSSourceFile> files = new ArrayList<JSSourceFile>();
        files.add(file);
        Result result = compiler.compile(externalJavascriptFiles, files, options);
        if (result.success) {
            w.write(compiler.toSource());
        } else {
            Minimizer.copy_(r, w);
        }
    }
}
