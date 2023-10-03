package ru.neotologia.server;

import ru.neotologia.clientHandler.ClientHandler;
import ru.neotologia.hendler.Handler;

import java.io.*;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {
    static int port;
    static ExecutorService threadPool = Executors.newFixedThreadPool(64);
    public static final Map<String, Map<String, Handler>> handlerList = new HashMap<>();



    public void run() {
        try (final var serverSocket = new ServerSocket(port)) {
            while (true) {
                try (final var socket = serverSocket.accept(); var in = new BufferedReader(new InputStreamReader(socket.getInputStream())); var out = new BufferedOutputStream(socket.getOutputStream());) {
                    threadPool.execute(new ClientHandler(in, out));

                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void listen(int port){
        Server.port = port;
        System.out.println("Server started on port " + port);
        start();
    }


    public synchronized void addHandler(String method, String path, Handler handler) {
        if (!handlerList.containsKey(path)) {
            handlerList.put(path, new HashMap<>());
        }
        if (!handlerList.get(path).containsKey(method)) {
            handlerList.get(path).put(method, handler);
        }
    }
}
