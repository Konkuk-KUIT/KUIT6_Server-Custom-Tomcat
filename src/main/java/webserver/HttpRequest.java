package webserver;

import webserver.enums.HttpMethod;

import java.io.BufferedReader;
import java.util.function.Predicate;

public class HttpRequest {
    public static HttpRequest from(BufferedReader br) {
        return new HttpRequest();
    }

    public HttpMethod getMethod() {
        return null;
    }

    public String getUrl() {
        return null;
    }
}
