package com.greenscriptool.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;

import com.greenscriptool.IFileLocator;
import com.greenscriptool.IResource;

public class FileResource implements IResource {
    
    public static class FileLocator implements IFileLocator {
        public File locate(String path) {
            return new File(path);
        }
    }
    
    public static FileLocator defFileLocator = new FileLocator();
    
    private File file_;
    public FileResource(File file) {
        if (null == file) throw new NullPointerException();
        file_ = file;
    }
    public FileResource(String path) {
        this(path, defFileLocator);
    }
    public FileResource(String path, IFileLocator fileLocator) {
        if (null == path || null == fileLocator) throw new NullPointerException();
        file_ = fileLocator.locate(path);
    }

    public Reader getReader() {
        try {
            return null == file_ ? null : new FileReader(file_);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public Writer getWriter() {
        try {
            return null == file_ ? null : new FileWriter(file_);
        } catch (Exception e) {
            return null;
        }
    }

    public String getKey() {
        return null == file_ ? null : file_.getName();
    }

}
