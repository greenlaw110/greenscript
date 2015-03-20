package com.greenscriptool;

import java.io.Reader;
import java.io.Writer;

public interface IResource {
    Reader getReader();
    Writer getWriter();
    String getKey();
}
