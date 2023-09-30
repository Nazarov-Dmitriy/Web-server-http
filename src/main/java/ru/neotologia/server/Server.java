package ru.neotologia.server;

import ru.neotologia.clientHandler.ClientHandler;

import java.io.*;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    static int port;
    static ExecutorService threadPool = Executors.newFixedThreadPool(64);

    public Server(int port) {
        Server.port = port;
        System.out.println("Server started on port " + port);
    }

    public void run() {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try (final var socket = serverSocket.accept();
                      var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                      var out = new BufferedOutputStream(socket.getOutputStream());) {
                    System.out.println("new coon");
                    threadPool.execute(new ClientHandler( in, out));
                    System.out.println(threadPool.toString());
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
