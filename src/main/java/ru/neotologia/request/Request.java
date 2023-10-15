package ru.neotologia.request;

import org.apache.http.NameValuePair;

import java.net.URISyntaxException;
import java.util.List;

public class Request {
    private final String method;
    private final String path;
    private List<NameValuePair> querys;

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

    public String getQueryParam(String query) throws URISyntaxException {
        String param = null;
        for (NameValuePair item : querys) {
            if (query.equals(item.toString().split("=")[0])) {
                param = item.toString().split("=")[1];
            }
        }
        return param;
    }
}
