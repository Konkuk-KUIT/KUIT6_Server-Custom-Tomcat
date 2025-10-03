package http;

import http.enums.HttpMethod;

public class HttpStartLine {
    private final HttpMethod method;
    private final String path;
    private final String version;

    public HttpStartLine(HttpMethod method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpStartLine from(String requestLine) {
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("Request line cannot be null or empty");
        }

        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid request line format: " + requestLine);
        }

        HttpMethod method = HttpMethod.from(parts[0]);
        String path = parts[1];
        String version = parts[2];

        return new HttpStartLine(method, path, version);
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

    public String getPathWithoutQuery() {
        if (path.contains("?")) {
            return path.substring(0, path.indexOf("?"));
        }
        return path;
    }

    public String getQueryString() {
        if (path.contains("?")) {
            return path.substring(path.indexOf("?") + 1);
        }
        return null;
    }
}