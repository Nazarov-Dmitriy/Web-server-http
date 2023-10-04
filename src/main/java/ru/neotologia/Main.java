package ru.neotologia;
import ru.neotologia.server.Server;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import static ru.neotologia.clientHandler.ClientHandler.getQueryParam;

public class Main {

    public static void main(String[] args) {
        var port = Integer.parseInt(readSettings());
        final var server = new Server();

        server.addHandler("GET", "/messages",
                (request, response) -> {
                    // Получение параметра из query
                    System.out.println(getQueryParam(request.getQuery(), "111"));

                    final var filePath = Path.of(".", "public", request.getPath() + ".html");
                    final var mimeType = Files.probeContentType(filePath);
                    final var length = Files.size(filePath);
                    response.write(("HTTP/1.1 " + 200 + "\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n").getBytes());

                    Files.copy(filePath, response);
                    response.flush();
                });

        server.addHandler("POST", "/messages",
                (request, response) -> {
                    final var filePath = Path.of(".", "public", request.getPath() + "Post.html");
                    final var mimeType = Files.probeContentType(filePath);
                    final var length = Files.size(filePath);
                    response.write(("HTTP/1.1 " + 200 + "\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n").getBytes());

                    Files.copy(filePath, response);
                    response.flush();
                });

        server.listen(port);
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

