package http;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest {
    private final HttpStartLine startLine;
    private final Map<String, String> headers;
    private final Map<String, String> params;
    private final Map<String, String> cookies;
    private final String body;

    private HttpRequest(HttpStartLine startLine,
                        Map<String, String> headers,
                        Map<String, String> params,
                        Map<String, String> cookies,
                        String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.params = params;
        this.cookies = cookies;
        this.body = body;
    }

    public static HttpRequest from(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        return from(br);
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        String startLineRaw = reader.readLine();
        if (startLineRaw == null || startLineRaw.isEmpty()) {
            throw new IOException("Empty HTTP request");
        }

        String[] tokens = startLineRaw.split(" ");
        if (tokens.length < 2) {
            throw new IOException("Invalid HTTP start line: " + startLineRaw);
        }

        String rawPath = tokens[1];
        String pathWithoutQuery = rawPath;
        Map<String, String> params = new HashMap<>();
        int queryIdx = rawPath.indexOf('?');
        if (queryIdx >= 0) {
            String query = rawPath.substring(queryIdx + 1);
            pathWithoutQuery = rawPath.substring(0, queryIdx);
            Map<String, String> queryParams = HttpRequestUtils.parseQueryParameter(query);
            if (queryParams != null) {
                params.putAll(queryParams);
            }
        }

        HttpStartLine startLine;
        try {
            startLine = HttpStartLine.of(tokens[0], pathWithoutQuery, tokens.length > 2 ? tokens[2] : "HTTP/1.1");
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid HTTP method: " + tokens[0], e);
        }

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int colon = line.indexOf(':');
            if (colon > 0) {
                String name = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                headers.put(name, value);
            }
        }

        Map<String, String> cookies = parseCookies(headers.get(HttpHeader.COOKIE.value()));

        String body = "";
        if (HttpMethod.POST == startLine.method()) {
            String cl = headers.get(HttpHeader.CONTENT_LENGTH.value());
            if (cl != null) {
                int contentLength = Integer.parseInt(cl.trim());
                body = IOUtils.readData(reader, contentLength);
                Map<String, String> form = HttpRequestUtils.parseQueryParameter(body);
                if (form != null) params.putAll(form);
            }
        }

        return new HttpRequest(startLine, headers, params, cookies, body);
    }

    public HttpMethod getMethod() {
        return startLine.method();
    }

    public String getPath() {
        return startLine.path();
    }

    public String getUrl() {
        return getPath();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getHeader(HttpHeader header) {
        return headers.get(header.value());
    }

    public String getParameter(String name) {
        return params.get(name);
    }

    public Map<String, String> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public Map<String, String> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }

    public String getBody() {
        return body;
    }

    private static Map<String, String> parseCookies(String cookieHeader) {
        Map<String, String> result = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return result;
        }
        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                result.put(kv[0].trim(), kv[1].trim());
            }
        }
        return result;
    }
}
