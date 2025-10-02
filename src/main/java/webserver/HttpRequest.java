package webserver;

import http.util.HttpRequestUtils;
import webserver.enums.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private HttpMethod method;
    private String path;
    private Map<String,String> cookies;
    private Map<String,String> params;

    public static HttpRequest from(BufferedReader br) throws IOException {
        return HttpRequestUtils.parse(br);
    }

    public static HttpRequest from(HttpMethod method, String path, Map<String,String> headers, String body) {
        HttpRequest request = new HttpRequest();
        request.method = method;
        request.path = path;
        request.cookies = headers.get("Cookie") != null ? HttpRequestUtils.parseCookies(headers.get("Cookie")) : new HashMap<>();
        request.params = !body.isBlank() ? HttpRequestUtils.parseFormData(body) : new HashMap<>();
        return request;
    }

    public HttpMethod getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public String getCookie(String key) {
        return this.cookies.get(key);
    }

    public String getParam(String key) {
        return this.params.get(key);
    }
}
