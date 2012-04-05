package com.greenscriptool.utils;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import com.greenscriptool.IResource;

public class BufferResource implements IResource {
	private static final long serialVersionUID = 3173516477770425002L;
    
    private String key_;
    private String buffer_;
    
    public BufferResource(String key) {
        if (null == key) throw new NullPointerException();
        key_ = key;
    }

    public Reader getReader() {
        return new StringReader(buffer_);
    }

    public Writer getWriter() {
        return new StringWriter(){
            @Override
            public void close() {
                BufferResource.this.buffer_ = this.toString();
                BufferResource.this.onWriterClose();
            }
        };
    }
    
    @Override
    public String toString() {
        return buffer_;
    }

    public String getKey() {
        return key_;
    }
    
    public static void main(String[] args) throws Exception {
        BufferResource br = new BufferResource("key");
        Writer w = br.getWriter();
        w.write("Hello World");
        w.close();
        System.out.println(br);
    }

    protected void onWriterClose() {
    	// Do nothing.
    }
}
