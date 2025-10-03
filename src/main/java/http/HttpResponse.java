package main.java.http;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import main.java.http.enums.HttpStatus;

public class HttpResponse {

    private final DataOutputStream dos;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private HttpStatus status;
    private byte[] body = new byte[0];

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String resourcePath) throws IOException {
        Path filePath = Paths.get("./webapp" + resourcePath);
        if (!Files.exists(filePath)) {
            String notFound = "<h1>404 Not Found</h1>";
            setStatus(HttpStatus.NOT_FOUND);
            setHeader("Content-Type", "text/html; charset=utf-8");
            setBody(notFound.getBytes());
            send();
            return;
        }
        byte[] body = Files.readAllBytes(filePath);
        String contentType = guessContentType(resourcePath);
        setStatus(HttpStatus.OK);
        setHeader("Content-Type", contentType);
        setBody(body);
        send();
    }

    public void redirect(String location) throws IOException {
        setStatus(HttpStatus.FOUND);
        setHeader("Location", location);
        setHeader("Content-Length", "0");
        send();
    }

    public void redirectWithCookie(String location, String cookie) throws IOException {
        setStatus(HttpStatus.FOUND);
        setHeader("Location", location);
        setHeader("Set-Cookie", cookie);
        setHeader("Content-Length", "0");
        send();
    }

    private void setStatus(HttpStatus status) { this.status = status; }
    private void setHeader(String k, String v) { headers.put(k, v); }
    private void setBody(byte[] body) {
        this.body = (body == null) ? new byte[0] : body;
        headers.put("Content-Length", String.valueOf(this.body.length));
    }

    private void send() throws IOException {
        dos.writeBytes(status.format());
        headers.putIfAbsent("Connection", "close");
        for (Map.Entry<String, String> e : headers.entrySet()) {
            dos.writeBytes(e.getKey() + ": " + e.getValue() + "\r\n");
        }
        dos.writeBytes("\r\n");
        if (body.length > 0) {
            dos.write(body);
        }
        dos.flush();
    }

    private String guessContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css")) return "text/css; charset=utf-8";
        if (path.endsWith(".js")) return "application/javascript; charset=utf-8";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }



}
