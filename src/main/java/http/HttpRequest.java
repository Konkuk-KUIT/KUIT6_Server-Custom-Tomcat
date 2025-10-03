package http;

import http.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final String url;
    private final String version;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String body;

    private HttpRequest(HttpMethod method, String url, String version,
                        Map<String, String> headers, String body, Map<String, String> parameters) {
        this.method = method;
        this.url = url;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.parameters = parameters;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        // Start line
        String startLine = br.readLine();
        if (startLine == null || startLine.isEmpty()) {
            throw new IOException("빈 요청");
        }
        String[] tokens = startLine.split(" ");
        HttpMethod method = HttpMethod.valueOf(tokens[0]);
        String url = tokens[1];
        String version = tokens[2];

        Map<String, String> headers = new HashMap<>();
        String line;
        int contentLength = 0;
        while (!(line = br.readLine()).equals("")) {
            String[] pair = line.split(":", 2);
            if (pair.length == 2) {
                headers.put(pair[0].trim(), pair[1].trim());
                if (pair[0].equalsIgnoreCase("Content-Length")) {
                    contentLength = Integer.parseInt(pair[1].trim());
                }
            }
        }

        String body = "";
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            body = new String(bodyChars);
        }

        Map<String, String> params = new HashMap<>();
        if (url.contains("?")) {
            String queryString = url.substring(url.indexOf("?") + 1);
            parseParams(queryString, params);
            url = url.substring(0, url.indexOf("?")); // url 정리
        }
        if (!body.isEmpty()) {
            parseParams(body, params);
        }

        return new HttpRequest(method, url, version, headers, body, params);
    }

    private static void parseParams(String data, Map<String, String> params) {
        for (String param : data.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }
}
