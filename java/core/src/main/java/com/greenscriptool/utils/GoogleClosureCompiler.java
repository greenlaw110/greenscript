package com.greenscriptool.utils;

import java.io.File;
import java.io.Writer;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.Result;

// GoogleClosureCompiler deal with only CSS file
public class GoogleClosureCompiler implements ICompressor {

   private static Compiler c_ = new Compiler();
   private static CompilerOptions o_ = new CompilerOptions();
   static {
      CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(o_);
   }

   @Override
   public void compress(File input, Writer output) throws Exception {
      JSSourceFile f = JSSourceFile.fromFile(input);
      Result r = c_.compile(null, f, o_);
      if (r.success) {
         output.write(c_.toSource());
      } else {
         StringBuffer sb = new StringBuffer();
         sb.append("GoogleClosure Compilation errors:");
         for (JSError e : r.errors) {
            sb.append("\n").append(
                  String.format("Line: #%1$s, %2$s", e.lineNumber,
                        e.description));
         }
         throw new Exception(sb.toString());
      }
   }

}
