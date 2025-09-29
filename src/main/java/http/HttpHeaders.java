package http;

import http.enums.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = new HashMap<>(headers);
    }

    public static HttpHeaders from(BufferedReader br) throws IOException {
        Map<String, String> headerMap = new HashMap<>();
        
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");
            if (colonIndex != -1) {
                String key = line.substring(0, colonIndex).trim();
                String value = line.substring(colonIndex + 1).trim();
                headerMap.put(key, value);
            }
        }
        
        return new HttpHeaders(headerMap);
    }

    public String getHeader(HttpHeader header) {
        return headers.get(header.getValue());
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public int getContentLength() {
        String contentLength = getHeader(HttpHeader.CONTENT_LENGTH);
        return contentLength != null ? Integer.parseInt(contentLength) : 0;
    }

    public String getCookie() {
        return getHeader(HttpHeader.COOKIE);
    }

    public boolean hasHeader(HttpHeader header) {
        return headers.containsKey(header.getValue());
    }

    public boolean hasHeader(String headerName) {
        return headers.containsKey(headerName);
    }

    public Map<String, String> getAllHeaders() {
        return new HashMap<>(headers);
    }
}