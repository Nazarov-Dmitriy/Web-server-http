package ru.neotologia.request;

public class Request {
    String method;
    String path;
    String body;

    public Request(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public Request(String method, String path, String body) {
        this.method = method;
        this.path = path;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }
}
