package http.request;

import webserver.HttpMethod;
import webserver.HttpHeader;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private HttpMethod method;
    private String url;
    private String version;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    private HttpRequest() {}

    // from 정적 팩토리 메서드
    public static HttpRequest from(BufferedReader br) throws IOException {
        HttpRequest request = new HttpRequest();
        // 1. 시작 라인 파싱
        String startLine = br.readLine();
        if (startLine == null) throw new IOException("Empty request");

        String[] parts = startLine.split(" ");
        request.method = HttpMethod.valueOf(parts[0]);
        request.url = parts[1];
        request.version = parts[2];

        // 2. 헤더 읽기
        String line;
        while (!(line = br.readLine()).equals("")) {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                request.headers.put(headerParts[0], headerParts[1]);
            }
        }

        // 3. 바디 읽기 (Content-Length 있으면)
        String contentLengthValue = request.headers.get(HttpHeader.CONTENT_LENGTH.getValue());
        if (contentLengthValue != null) {
            int length = Integer.parseInt(contentLengthValue);
            char[] bodyChars = new char[length];
            int readLen = br.read(bodyChars, 0, length);
            request.body = new String(bodyChars, 0, readLen);
        }

        return request;
    }

    // getter 메서드들
    public HttpMethod getMethod() { return method; }
    public String getUrl() { return url; }
    public String getVersion() { return version; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }

    // 쿼리스트링 파싱 (예: /user/signup?userId=aaa&password=bbb)
    public Map<String, String> getQueryParameters() {
        String[] urlParts = url.split("\\?", 2);
        Map<String, String> paramMap = new HashMap<>();
        if (urlParts.length < 2) return paramMap;
        String queryString = urlParts[1];
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    paramMap.put(URLDecoder.decode(keyValue[0], "UTF-8"), URLDecoder.decode(keyValue[1], "UTF-8"));
                } catch (Exception e) {
                    paramMap.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return paramMap;
    }

    // POST에서 바디 파싱 위한 메서드
    public Map<String, String> getBodyParameters() {
        Map<String, String> paramMap = new HashMap<>();
        if (body == null) return paramMap;
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                try {
                    paramMap.put(URLDecoder.decode(keyValue[0], "UTF-8"), URLDecoder.decode(keyValue[1], "UTF-8"));
                } catch (Exception e) {
                    paramMap.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return paramMap;
    }
}

