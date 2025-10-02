package webserver;

import webserver.enums.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponse {
    private final DataOutputStream dos;
    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void setStatus(HttpStatus status) throws IOException {
        switch (status) {
            case OK_200 -> dos.writeBytes("HTTP/1.1 200 OK\r\n");
            case FOUND_302 -> dos.writeBytes("HTTP/1.1 302 Found\r\n");
            case BAD_REQUEST_400 -> dos.writeBytes("HTTP/1.1 400 Bad Request\r\n");
            case NOT_FOUND_404  -> dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            case INTERNAL_SERVER_ERROR_500 -> dos.writeBytes("HTTP/1.1 500 Internal Server Error\r\n");
            default -> throw new IllegalArgumentException("Invalid status code: " + status);
        }
    }

    public void setHeader(String key, String value) throws IOException {
        dos.writeBytes(key + ": " + value + "\r\n");

    }

    public void setBody(byte[] body) throws IOException {
        dos.writeBytes("\r\n");
        dos.write(body, 0, body.length);
        dos.flush();
    }

    public void setBody() throws IOException {
        dos.writeBytes("\r\n");
        dos.flush();
    }

}
