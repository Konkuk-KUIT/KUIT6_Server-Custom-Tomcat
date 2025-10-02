package http.request;

import http.constant.HttpMethod;

public class HttpStartLine {
    private final HttpMethod method;
    private final String path;
    private final String version;

    private HttpStartLine(HttpMethod method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpStartLine from(String requestLine) {
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IllegalArgumentException("Request line cannot be empty");
        }

        String[] tokens = requestLine.split(" ");
        if (tokens.length != 3) {
            throw new IllegalArgumentException("Invalid request line format");
        }

        HttpMethod method = HttpMethod.from(tokens[0]);
        String path = tokens[1];
        String version = tokens[2];

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
            return path.split("\\?")[0];
        }
        return path;
    }

    public String getQueryString() {
        if (path.contains("?")) {
            return path.split("\\?", 2)[1];
        }
        return "";
    }
}