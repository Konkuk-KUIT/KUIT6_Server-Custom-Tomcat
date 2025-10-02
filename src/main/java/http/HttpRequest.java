package http;


import enums.HttpMethod;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;

public class HttpRequest {
    private final HttpStartLine startLine;
    private final HttpHeaderMap header;
    private final HttpBody body;

    private HttpRequest(HttpStartLine startLine, HttpHeaderMap header, HttpBody body) {
        this.startLine = startLine;
        this.header = header;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String request = br.readLine();
        if (request == null) throw new EOFException("Empty request...");
        HttpStartLine s = HttpStartLine.parse(request);
        HttpHeaderMap h = HttpHeaderMap.parse(br);

        int length = h.getContentLength();
        HttpBody b = HttpBody.readBody(br, length);

        return new HttpRequest(s,h,b);

    }


    public HttpStartLine getStartLine() {
        return startLine;
    }

    public HttpHeaderMap getHeader() {
        return header;
    }

    public HttpBody getBody() {
        return body;
    }

    public HttpMethod getMethod(){
        return startLine.getMethod();
    }

    public String getUrl() {
        return startLine.getPath();
    }
}
