package ru.neotologia.request;

import org.apache.http.NameValuePair;

import java.util.List;

public class Request {
    String method;
    String path;
    List<NameValuePair> querys;

    public Request(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public Request(String method, String path, List<NameValuePair> querys) {
        this.method = method;
        this.path = path;
        this.querys = querys;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<NameValuePair> getQuery() {
        return querys;
    }
}
