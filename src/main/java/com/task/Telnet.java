package com.task;

import com.task.resources.TelnetServerResource;
import com.task.resources.impl.TelnetServerResourceImpl;
import com.task.service.FileSearchService;
import com.task.service.impl.FileSearchServiceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
public class Telnet {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Telnet <portNumber> <rootPath>");
            return;
        }
        int portNumber = Integer.parseInt(args[0]);
        String rootPath = args[1];

        File rootDir = new File(rootPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            log.error("Invalid root directory.");
            return;
        }

        FileSearchService fileSearchService = new FileSearchServiceImpl();
        TelnetServerResource telnetServerResource = new TelnetServerResourceImpl(fileSearchService);
        telnetServerResource.start(portNumber, rootDir);
    }


}
