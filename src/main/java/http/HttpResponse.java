package http;

public class HttpResponse {
    private final HttpStartLine startLine;
    private final HttpHeaderMap header;
    private final HttpBody body;

    private HttpResponse(HttpStartLine startLine, HttpHeaderMap header, HttpBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }
}
