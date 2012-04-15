package com.greenscriptool;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;

public interface IResource extends Serializable {
    Reader getReader();
    Writer getWriter();
    String getKey();
}
