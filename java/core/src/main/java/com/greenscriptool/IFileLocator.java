package com.greenscriptool;

import java.io.File;

public interface IFileLocator {
	File locate(String relativePath);
}
