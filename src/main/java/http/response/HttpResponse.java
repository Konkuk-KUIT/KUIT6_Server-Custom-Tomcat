package http.response;

import webserver.HttpHeader;
import webserver.HttpStatusCode;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponse {
    private DataOutputStream dos;

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) throws IOException {


        String filePath = Paths.get("webapp", path.startsWith("/") ? path.substring(1) : path).toString();
        byte[] body;
        String contentType = "text/html;charset=utf-8";
        System.out.println("HttpResponse.forward called with path: " + path);
        System.out.println("Resolved file path: " + filePath);

        if (Files.exists(Paths.get(filePath)) && !Files.isDirectory(Paths.get(filePath))) {
            body = Files.readAllBytes(Paths.get(filePath));
            if (filePath.endsWith(".css")) {
                contentType = "text/css";
            } else if (filePath.endsWith(".js")) {
                contentType = "application/javascript";
            }
        } else {
            body = "<html><body>404 Not Found</body></html>".getBytes();
        }

        response200Header(body.length, contentType);
        responseBody(body);
    }

    public void redirect(String path) throws IOException {
        dos.writeBytes("HTTP/1.1 " + HttpStatusCode.FOUND.getCode() + " " + HttpStatusCode.FOUND.getReason() + "\r\n");
        dos.writeBytes(HttpHeader.LOCATION.getValue() + ": " + path + "\r\n");
        dos.writeBytes("\r\n");
        dos.flush();
    }

    public void redirectWithCookie(String path, String cookie) throws IOException {
        dos.writeBytes("HTTP/1.1 " + HttpStatusCode.FOUND.getCode() + " " + HttpStatusCode.FOUND.getReason() + "\r\n");
        dos.writeBytes(HttpHeader.LOCATION.getValue() + ": " + path + "\r\n");
        dos.writeBytes(HttpHeader.SET_COOKIE.getValue() + ": " + cookie + "\r\n");
        dos.writeBytes("\r\n");
        dos.flush();
    }

    private void response200Header(int lengthOfBodyContent, String contentType) throws IOException {
        dos.writeBytes("HTTP/1.1 " + HttpStatusCode.OK.getCode() + " " + HttpStatusCode.OK.getReason() + "\r\n");
        dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": " + contentType + "\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
