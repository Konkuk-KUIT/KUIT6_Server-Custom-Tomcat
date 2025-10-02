package http.request;

import http.constant.HttpMethod;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private final HttpStartLine startLine;
    private final HttpHeaders headers;
    private final String body;

    private HttpRequest(HttpStartLine startLine, HttpHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        // 1. StartLine 읽기
        String requestLine = br.readLine();
        HttpStartLine startLine = HttpStartLine.from(requestLine);

        // 2. Headers 읽기
        HttpHeaders headers = HttpHeaders.from(br);

        // 3. Body 읽기
        String body = "";
        int contentLength = headers.getContentLength();
        if (contentLength > 0) {
            body = IOUtils.readData(br, contentLength);
        }

        return new HttpRequest(startLine, headers, body);
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getPath() {
        return startLine.getPathWithoutQuery();
    }

    public String getUrl() {
        return startLine.getPath();
    }

    public String getQueryString() {
        return startLine.getQueryString();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public String getCookie(String cookieName) {
        String cookieHeader = headers.getCookie();
        if (cookieHeader == null) {
            return null;
        }

        String[] cookies = cookieHeader.split("; ");
        for (String cookie : cookies) {
            String[] keyValue = cookie.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(cookieName)) {
                return keyValue[1];
            }
        }
        return null;
    }
}