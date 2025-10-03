package webserver;

import http.util.HttpRequestUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, String> params;
    private final Map<String, String> cookies;

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        String line = br.readLine();
        if (line == null) throw new IOException("Empty request");

        String[] tokens = line.split(" ");
        this.method = tokens[0];
        String url = tokens[1];

        int queryStringIndex = url.indexOf('?');
        if (queryStringIndex != -1) {
            this.path = url.substring(0, queryStringIndex);
            this.params = HttpRequestUtils.parseQueryString(url.substring(queryStringIndex + 1));
        } else {
            this.path = url;
            this.params = new HashMap<>();
        }

        int contentLength = 0;
        while (!(line = br.readLine()).isEmpty()) {
            String[] headerTokens = line.split(": ");
            headers.put(headerTokens[0], headerTokens[1]);
            if ("Content-Length".equals(headerTokens[0])) {
                contentLength = Integer.parseInt(headerTokens[1]);
            }
        }

        this.cookies = HttpRequestUtils.parseCookies(headers.getOrDefault("Cookie", ""));

        if (contentLength > 0) {
            char[] body = new char[contentLength];
            br.read(body);
            this.params.putAll(HttpRequestUtils.parseQueryString(new String(body)));
        }
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }
    public String getParameter(String key) { return params.get(key); }
    public boolean isLoggedIn() { return Boolean.parseBoolean(cookies.get("logined")); }
}