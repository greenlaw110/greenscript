package com.greenscriptool.utils;

import java.util.List;

import com.greenscriptool.IResourceLocator;

public interface IBufferLocator extends IResourceLocator {
    
	BufferResource locate(String key);
    
    BufferResource newBuffer(List<String> resourceNames, String extension);
}
