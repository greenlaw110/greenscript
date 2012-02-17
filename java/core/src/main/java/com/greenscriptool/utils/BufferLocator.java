package com.greenscriptool.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BufferLocator implements IBufferLocator {
    Map<String, BufferResource> buffers = new HashMap<String, BufferResource>();
    
    @Override
    public BufferResource locate(String key) {
        return this.buffers.get(key);
    }
    
    @Override
    public BufferResource newBuffer(List<String> resourceNames, String extension) {
        String key = UUID.randomUUID().toString() + extension;
        BufferResource buffer = new BufferResource(key);
        this.buffers.put(key, buffer);
        return buffer;
    }

}
