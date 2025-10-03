package http;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params = new HashMap<>();
    private final Map<String, String> cookies = new HashMap<>();

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String startLine = br.readLine();
        if (startLine == null || startLine.isEmpty()) {
            return;
        }
        String[] tokens = startLine.split(" ");
        this.method = tokens[0];
        this.path = tokens.length > 1 ? tokens[1] : "/";

        int qIdx = this.path.indexOf('?');
        if (qIdx >= 0) {
            String query = this.path.substring(qIdx + 1);
            this.path = this.path.substring(0, qIdx);
            Map<String, String> qs = HttpRequestUtils.parseQueryParameter(query);
            if (qs != null) params.putAll(qs);
        }

        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int colon = line.indexOf(':');
            if (colon > 0) {
                String name = line.substring(0, colon).trim();
                String value = line.substring(colon + 1).trim();
                headers.put(name, value);
            }
        }

        parseCookies();

        if ("POST".equalsIgnoreCase(method)) {
            String cl = headers.get("Content-Length");
            if (cl != null) {
                int contentLength = Integer.parseInt(cl.trim());
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> form = HttpRequestUtils.parseQueryParameter(body);
                if (form != null) params.putAll(form);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getParameter(String name) {
        return params.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }

    private void parseCookies() {
        String cookieHeader = headers.get("Cookie");
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return;
        }
        String[] pairs = cookieHeader.split(";");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                cookies.put(kv[0].trim(), kv[1].trim());
            }
        }
    }
}
