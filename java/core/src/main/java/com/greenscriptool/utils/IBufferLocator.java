package com.greenscriptool.utils;

import com.greenscriptool.IResourceLocator;

public interface IBufferLocator extends IResourceLocator {
    @Override
    BufferResource locate(String key);
    
    BufferResource newBuffer(String extension);
}
