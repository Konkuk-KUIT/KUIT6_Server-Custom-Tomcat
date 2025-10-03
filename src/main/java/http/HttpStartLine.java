package http;

public class HttpStartLine {
    private final String method;
    private final String path;
    private final String version;

    private HttpStartLine(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpStartLine parse(String line) {
        String[] parts = line.split(" ");
        String method = parts.length > 0 ? parts[0] : "";
        String path = parts.length > 1 ? parts[1] : "";
        String version = parts.length > 2 ? parts[2] : "";
        return new HttpStartLine(method, path, version);
    }

    public String method() { return method; }
    public String path() { return path; }
    public String version() { return version; }
}
