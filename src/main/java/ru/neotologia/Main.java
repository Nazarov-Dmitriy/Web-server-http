package ru.neotologia;

import ru.neotologia.server.Server;

import java.io.*;

public class Main {

    public static void main(String[] args) {
        var port = Integer.parseInt(readSettings());
        Server server = new Server(port);
        server.start();

    }

    public static String readSettings() {
        String str;
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            str = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return str.replaceAll("[^\\d]", "");
    }
}

