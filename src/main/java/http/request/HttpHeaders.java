package http.request;

import http.constant.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> headers;

    private HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();

        while (true) {
            String line = br.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }

            int colonIndex = line.indexOf(":");
            if (colonIndex > 0) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headers.put(key, value);
            }
        }

        return new HttpHeaders(headers);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public int getContentLength() {
        String contentLength = headers.get(HttpHeader.CONTENT_LENGTH.getValue());
        if (contentLength == null) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }

    public String getCookie() {
        return headers.get(HttpHeader.COOKIE.getValue());
    }

    public boolean hasHeader(String key) {
        return headers.containsKey(key);
    }
}