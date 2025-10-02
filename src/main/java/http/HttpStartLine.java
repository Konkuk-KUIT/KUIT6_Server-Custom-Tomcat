package http;

import enums.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class HttpStartLine {
    private final HttpMethod method;
    private final String path;
    private final String version;

    public HttpStartLine(HttpMethod method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpStartLine parse(String line){
        String[] tokens = line.split(" ");
        if (tokens.length != 3) throw new IllegalArgumentException("Invalid start line: " + line);
        return new HttpStartLine(
                HttpMethod.from(tokens[0]),
                tokens[1],
                tokens[2]
        );
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getQueryParams() {
        Map<String, String> params = new HashMap<>();
        String[] parts = path.split("\\?", 2);
        if (parts.length < 2) return params;

        String queryString = parts[1];
        for (String pair : queryString.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) params.put(kv[0], kv[1]);
        }
        return params;
    }
}
