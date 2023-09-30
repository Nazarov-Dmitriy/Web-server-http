package ru.neotologia.clientHandler;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class ClientHandler implements Runnable {
    BufferedReader in;
    BufferedOutputStream out;
    List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");

    public ClientHandler(BufferedReader in, BufferedOutputStream out) throws IOException {

        this.in = in;
        this.out = out;
        run();
    }


    @Override
    public void run() {

        while (true) {
            try {
                final var requestLine = in.readLine();
                if (requestLine == null) {
                    break;
                }

                final var parts = requestLine.split(" ");

                if (parts.length != 3) {
                    continue;
                }

                final var path = parts[1];
                if (!validPaths.contains(path)) {
                    responseHeaders("404 Not Found", 0);
                    continue;
                }

                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);

                byte[] replaseClassicHtml = replaceHtml("/classic.html", filePath, "{time}", LocalDateTime.now().toString());

                if (replaseClassicHtml.length != 0) {
                    responseHeaders("200 OK", mimeType, replaseClassicHtml.length, replaseClassicHtml);
                    continue;
                }

                final var length = Files.size(filePath);
                responseHeaders("200 OK", mimeType, length, filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public byte[] replaceHtml(String path, Path filePath, String str, String replace) throws IOException {
        byte[] content = new byte[0];
        if (validPaths.contains(path)) {
            final var template = Files.readString(filePath);
            content = template.replace(
                    str,
                    replace
            ).getBytes();
        }
        return content;
    }


    public void responseHeaders(String statusCode, String contentType, long length, Path filePath) throws IOException {
        out.write(("HTTP/1.1 " + statusCode + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n").getBytes());

        Files.copy(filePath, out);
        out.flush();
    }

    public void responseHeaders(String statusCode, String contentType, int length, byte[] content) throws IOException {
        out.write(("HTTP/1.1 " + statusCode + "\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n").getBytes());

        out.write(content);
        out.flush();
    }

    public void responseHeaders(String statusCode, int length) throws IOException {
        out.write(("HTTP/1.1 " + statusCode + "\r\n" +
                "Content-Length: " + length + "\r\n" +
                "Connection: close\r\n" +
                "\r\n").getBytes());
        out.flush();
    }
}
