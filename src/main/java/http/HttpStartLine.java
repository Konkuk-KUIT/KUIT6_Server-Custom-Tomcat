package http;

import java.util.Locale;

public class HttpStartLine {
    private final HttpMethod method;
    private final String path;
    private final String version;

    private HttpStartLine(HttpMethod method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpStartLine of(String methodToken, String pathToken, String versionToken) {
        HttpMethod method = HttpMethod.valueOf(methodToken.toUpperCase(Locale.ROOT));
        String path = (pathToken == null || pathToken.isEmpty()) ? "/" : pathToken;
        String version = versionToken == null ? "HTTP/1.1" : versionToken;
        return new HttpStartLine(method, path, version);
    }

    public HttpMethod method() {
        return method;
    }

    public String path() {
        return path;
    }

    public String version() {
        return version;
    }
}