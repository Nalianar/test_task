package com.task.service.impl;

import com.task.service.FileSearchService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class FileSearchServiceImpl implements FileSearchService {

    private static boolean isSearchDone = false;
    private final Queue<String> fileQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void searchFiles(File rootDir, int depth, String mask, PrintWriter out) {

        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(()-> {
            search(depth, mask, rootDir, fileQueue);
            isSearchDone = true;  //Adding marker when search is stopped
        });
        executor.submit(()-> {
            while (!isSearchDone) {
                if(fileQueue.peek() != null) {
                    out.println(fileQueue.poll());
                }
            }

        });
        executor.shutdown();
        isSearchDone = false;
    }

    private void search(int depth, String mask, File rootDir, Queue<String> fileQueue) {
        Set<File> children = Set.of(rootDir.listFiles());
        if (!children.isEmpty()) {
            for (int i = depth; i > 0; i--) {
                children = children.stream()
                        .flatMap(x -> Stream.of((x.listFiles() != null)? x.listFiles() : new File[0]))
                        .collect(Collectors.toSet());
                children.forEach(x-> {
                    if(!x.isDirectory() && x.getName().contains(mask)){
                        fileQueue.add(x.toString());
                    }
                });
            }
        }
    }

}
