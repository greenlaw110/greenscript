package com.greenscriptool.utils;

import java.io.File;

import com.greenscriptool.IResource;
import com.greenscriptool.IResourceLocator;

public class FileResourceLocator implements IResourceLocator {

    @Override
    public IResource locate(String key) {
        return new FileResource(new File(key));
    }

}
