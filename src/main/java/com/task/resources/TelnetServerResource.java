package com.task.resources;

import java.io.File;

public interface TelnetServerResource {
    void start(int portNumber, File rootDir);
}
