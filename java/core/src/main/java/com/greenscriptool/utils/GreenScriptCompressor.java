package com.greenscriptool.utils;

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

public class GreenScriptCompressor implements ICompressor {
   private static Log logger_ = LogFactory.getLog(ICompressor.class);

   private ResourceType type_;

   public GreenScriptCompressor(ResourceType type) {
      type_ = type;
   }

   public void compress(Reader r, Writer w) throws Exception {
      if (null == r || null == w)
         throw new NullPointerException();
      try {
         switch (type_) {
         case CSS:
            new CssCompressor(r).compress(w, -1);
            break;
         case JS:
             new JavaScriptCompressor(r, er_).compress(w, -1, true,
                     false, false, false);
            break;
         default:
            throw new RuntimeException("Resource type not recognized: "
                  + type_.name());
         }
      } finally {
         if (null != r) {
            try {
               r.close();
            } catch (IOException e) {
               logger_.warn("error closing reader", e);
            }
         }
      }
   }

   private static ErrorReporter er_ = new ErrorReporter() {

      public void warning(String message, String sourceName, int line,
            String lineSource, int lineOffset) {
         if (line < 0) {
            logger_.warn("[MINIMIZOR.WARNING] " + message);
         } else {
            logger_.warn(String.format("[MINIMIZOR.WARNING] %1$s: %2$s: %3$s",
                  line, lineOffset, message));
         }
      }

      public void error(String message, String sourceName, int line,
            String lineSource, int lineOffset) {
         if (line < 0) {
            logger_.error("[MINIMIZOR.ERROR] " + message);
         } else {
            logger_.error(String.format("[MINIMIZOR.ERROR] %1$s: %2$s: %3$s",
                  line, lineOffset, message));
         }
      }

      public EvaluatorException runtimeError(String message, String sourceName,
            int line, String lineSource, int lineOffset) {
         error(message, sourceName, line, lineSource, lineOffset);
         return new EvaluatorException(message);
      }
   };

}
