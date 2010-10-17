package com.greenscriptool.utils;

import java.io.File;
import java.io.Writer;

/**
 * A <code>ICompressor</code> provides ability to compress resources
 * 
 * @author greenlaw110@gmail.com
 * @version 1.0, 2010-10-14
 * @since 1.0
 */
public interface ICompressor {
    /**
     * Read input, do compressing, and write to output.
     * 
     * <code>ICompressor</code> is responsible for open input file, read 
     * and process it and close the file after process finished. On the
     * other side, compressor does not "open" a output, neither does it
     * close it. This design enable the system to merge multiple resources
     * into one resource and compress them at the same time
     * 
     * @param input Input file
     * @param output where the compressed result write to
     * 
     * @throws Exception when error encountered during compressing process
     */
    void compress(File input, Writer output) throws Exception;
}
