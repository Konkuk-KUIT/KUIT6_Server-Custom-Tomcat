package http;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private final HttpStartLine startLine;
    private final HttpHeader header;
    private final String body;

    private HttpRequest(HttpStartLine startLine, HttpHeader header, String body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
    }
}
