package http;

import http.util.IOUtils;

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
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) return null;

        HttpHeader httpHeader = HttpHeader.from(br);
        String body = IOUtils.readData(br, httpHeader.getLengthOfContent());

        return new HttpRequest(HttpStartLine.from(requestLine), httpHeader, body);
    }

    public String getMethod(){
        return startLine.getMethod();
    }

    public String getUrl(){
        return startLine.getTarget();
    }

    public String getBody() {
        return body;
    }

    public HttpHeader getHeader(){
        return header;
    }
}
