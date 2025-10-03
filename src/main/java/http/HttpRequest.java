package main.java.http;

import main.java.http.enums.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpRequest {
    private final HttpMethod method;
    private final String rawPath;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    private final String path;
    private final String query;
    private final Map<String, String> params;
    private final Map<String, String> cookies;

    private HttpRequest(HttpMethod method, String rawPath, String version,
                        Map<String, String> headers, String body,
                        String path, String query,
                        Map<String, String> params, Map<String, String> cookies) {
        this.method = method;
        this.rawPath = rawPath;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.path = path;
        this.query = query;
        this.params = params;
        this.cookies = cookies;
    }


    public static HttpRequest from(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request line");
        }
        String[] tokens = requestLine.split(" ");
        if (tokens.length < 3) throw new IOException("Invalid request line: " + requestLine);

        HttpMethod method = HttpMethod.from(tokens[0]);
        String rawPath = tokens[1];
        String version = tokens[2];

        Map<String, String> headers = readHeaders(br);

        int contentLength = parseInt(headers.getOrDefault("Content-Length", "0"), 0);
        String body = readBody(br, contentLength);

        String[] pq = splitPathAndQuery(rawPath);
        String normalizedPath = normalizePath(pq[0]);
        String query = pq.length > 1 ? pq[1] : "";

        Map<String, String> params = new LinkedHashMap<>();
        if (!query.isEmpty()) {
            params.putAll(parseQueryString(query));
        }
        String ctype = headers.getOrDefault("Content-Type", "");
        if (!body.isEmpty() && ctype.startsWith("application/x-www-form-urlencoded")) {
            params.putAll(parseQueryString(body));
        }

        Map<String, String> cookies = parseCookie(headers.get("Cookie"));

        return new HttpRequest(
                method, rawPath, version, headers, body,
                normalizedPath, query, params, cookies
        );


    }

    private static Map<String, String> readHeaders(BufferedReader br) throws IOException {
        Map<String, String> headers = new LinkedHashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(':');
            if (idx > 0) {
                String key = line.substring(0, idx).trim();
                String val = line.substring(idx + 1).trim();
                headers.put(key, val);
            }
        }
        return headers;
    }

    private static String readBody(BufferedReader br, int contentLength) throws IOException {
        if (contentLength <= 0) return "";
        char[] buf = new char[contentLength];
        int off = 0;
        while (off < contentLength) {
            int r = br.read(buf, off, contentLength - off);
            if (r == -1) break;
            off += r;
        }
        return new String(buf, 0, off);
    }

    private static String[] splitPathAndQuery(String rawPath) {
        int q = rawPath.indexOf('?');
        if (q < 0) return new String[]{rawPath};
        return new String[]{rawPath.substring(0, q), rawPath.substring(q + 1)};
    }

    private static String normalizePath(String rawPath) {
        String p = rawPath.replaceAll("/+", "/");
        if (p.length() > 1 && p.endsWith("/")) p = p.substring(0, p.length() - 1);
        return p;
    }

    private static Map<String, String> parseQueryString(String qs) {
        Map<String, String> map = new LinkedHashMap<>();
        if (qs == null || qs.isEmpty()) return map;
        for (String pair : qs.split("&")) {
            if (pair.isEmpty()) continue;
            String[] kv = pair.split("=", 2);
            String k = urlDecode(kv[0]);
            String v = kv.length > 1 ? urlDecode(kv[1]) : "";
            map.put(k, v);
        }
        return map;
    }

    private static Map<String, String> parseCookie(String cookieHeader) {
        Map<String, String> map = new LinkedHashMap<>();
        if (cookieHeader == null || cookieHeader.isEmpty()) return map;
        String[] parts = cookieHeader.split(";");
        for (String part : parts) {
            int eq = part.indexOf('=');
            if (eq < 0) continue;
            String k = part.substring(0, eq).trim();
            String v = part.substring(eq + 1).trim();
            map.put(k, v);
        }
        return map;
    }

    private static String urlDecode(String s) {
        return s == null ? null : URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return def; }
    }

    public HttpMethod getMethod() { return method; }
    public String getRawPath() { return rawPath; }
    public String getVersion() { return version; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }

    public String getPath() { return path; }
    public String getQuery() { return query; }
    public Map<String, String> getParams() { return params; }
    public Map<String, String> getCookies() { return cookies; }

    public String getHeader(String name) { return headers.get(name); }
    public String getParam(String name) { return params.get(name); }
    public String getCookie(String name) { return cookies.get(name); }
}
