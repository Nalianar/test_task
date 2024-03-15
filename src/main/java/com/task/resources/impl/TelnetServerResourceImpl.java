package com.task.resources.impl;

import com.task.resources.TelnetServerResource;
import com.task.service.FileSearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Slf4j
public class TelnetServerResourceImpl implements TelnetServerResource {

    private final FileSearchService fileSearchService;

    @Override
    public void start(int portNumber, File rootDir){
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            log.info("Telnet server started on port " + portNumber);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                CompletableFuture.runAsync(() -> handleClient(clientSocket, rootDir));
            }
        } catch (IOException ex) {
            log.error("Error starting the server on port " + portNumber, ex);
        }
    }

    private void handleClient(Socket clientSocket, File rootDir) {
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {

            out.println("Welcome to the Telnet server! Please input depth of search and file mask separated with space.");
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                String[] depthAndMask = inputLine.split(" ",2);
                int depth = 0;
                try{
                    depth = Integer.parseInt(depthAndMask[0]);
                } catch (Exception ex){
                    out.println("Depth should be integer, try again");
                    continue;
                }
                String mask = depthAndMask[1].trim();

                out.println("Server received depth: " + depthAndMask[0] + ", and mask: " + depthAndMask[1]);
                fileSearchService.searchFiles(rootDir, depth, mask, out);

                if (inputLine.equals("exit")) {
                    break;
                }
            }
            clientSocket.close();
        } catch (IOException ex) {
            log.error("Unexpected exception occurred", ex);
        }
    }
}
