package ru.neotologia.clientHandler;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import ru.neotologia.request.Request;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static ru.neotologia.server.Server.handlerList;

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

                final var method = parts[0];
                var path = parts[1];
                List<NameValuePair> querys = null;

                if (method.equals("GET") && path.contains("?")) {
                    querys = getQueryParams(path);
//                    String query = getQueryParam(querys, "sss");
//                    System.out.println(query);
                    path = path.split("\\?")[0];
                }

                var request = new Request(method, path, querys);

                if (!validPaths.contains(path) && !handlerList.containsKey(path)) {
                    responseHeaders("404 Not Found", 0);
                    continue;
                }

                if (handlerList.containsKey(path) && handlerList.get(path).containsKey(method)) {
                    handlerList.get(path).get(method).handle(request, out);
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
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<NameValuePair> getQueryParams(String path) throws URISyntaxException {
        return URLEncodedUtils.parse(new URI(path), StandardCharsets.UTF_8);
    }

    public static String getQueryParam(List<NameValuePair> querys, String query) throws URISyntaxException {
        String param = null;
        for (NameValuePair item : querys) {
            if (query.equals(item.toString().split("=")[0])) {
                param = item.toString().split("=")[1];
            }
        }
        return param;
    }

    public byte[] replaceHtml(String path, Path filePath, String str, String replace) throws IOException {
        byte[] content = new byte[0];
        if (validPaths.contains(path)) {
            final var template = Files.readString(filePath);
            content = template.replace(str, replace).getBytes();
        }
        return content;
    }


    public void responseHeaders(String statusCode, String contentType, long length, Path filePath) throws IOException {
        out.write(("HTTP/1.1 " + statusCode + "\r\n" + "Content-Type: " + contentType + "\r\n" + "Content-Length: " + length + "\r\n" + "Connection: close\r\n" + "\r\n").getBytes());

        Files.copy(filePath, out);
        out.flush();
    }

    public void responseHeaders(String statusCode, String contentType, int length, byte[] content) throws IOException {
        out.write(("HTTP/1.1 " + statusCode + "\r\n" + "Content-Type: " + contentType + "\r\n" + "Content-Length: " + length + "\r\n" + "Connection: close\r\n" + "\r\n").getBytes());

        out.write(content);
        out.flush();
    }

    public void responseHeaders(String statusCode, int length) throws IOException {
        out.write(("HTTP/1.1 " + statusCode + "\r\n" + "Content-Length: " + length + "\r\n" + "Connection: close\r\n" + "\r\n").getBytes());
        out.flush();
    }
}
