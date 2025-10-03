package http;

import enums.HttpHeader;
import enums.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponse {
    private final DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) throws IOException {
        byte[] body = Files.readAllBytes(Paths.get("webapp" + path));

        // StartLine
        writeStatus(HttpStatus.OK);
        // Headers
        if (path.endsWith(".css")) {
            writeHeader(HttpHeader.CONTENT_TYPE, "text/css");
        } else {
            writeHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
        }
        writeHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(body.length));
        endHeaders();
        // Body
        writeBody(body);
    }

    public void sendRedirect(String redirectPath) throws IOException {
        writeStatus(HttpStatus.FOUND);
        writeHeader(HttpHeader.LOCATION, redirectPath);
        endHeaders();
    }

    public void sendNotFound() throws IOException {
        String body = "<h1>404 Not Found</h1>";
        writeStatus(HttpStatus.NOT_FOUND);
        writeHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
        writeHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
        endHeaders();
        writeBody(body.getBytes());
    }

    public void writeStatus(HttpStatus status) throws IOException {
        dos.writeBytes(status.toStatusLine());
    }

    public void writeHeader(HttpHeader headerName, String headerValue) throws IOException {
        dos.writeBytes(headerName.getValue() + ": " + headerValue + "\r\n");
    }

    public void endHeaders() throws IOException {
        dos.writeBytes("\r\n");
    }

    public void writeBody(byte[] body) throws IOException {
        dos.write(body);
        dos.flush();
    }

    public void sendRedirectWithCookie(String redirectPath, String cookie) throws IOException {
        writeStatus(HttpStatus.FOUND);
        writeHeader(HttpHeader.LOCATION, redirectPath);
        writeHeader(HttpHeader.SET_COOKIE, cookie);
        endHeaders();
    }

}
