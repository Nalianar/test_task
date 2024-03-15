package com.task.service;

import java.io.File;
import java.io.PrintWriter;

public interface FileSearchService {
    void searchFiles(File rootDir, int depth, String mask, PrintWriter out);
}
