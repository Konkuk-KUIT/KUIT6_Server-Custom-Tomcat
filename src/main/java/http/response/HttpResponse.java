package http.response;

import http.enums.HttpHeader;
import http.enums.HttpStatus;
import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    private final DataOutputStream dos;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void forward(String path) {
        if (path.equals("/")) {
            path = "/index.html";
        }
        try {
            File file = new File("./webapp" + path);
            if (file.exists()) {
                byte[] body = Files.readAllBytes(file.toPath());
                response200Header(body.length, getContentType(path));
                responseBody(body);
            } else {
                byte[] body = "<h1>404 Not Found</h1>".getBytes();
                response404Header(body.length);
                responseBody(body);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirect(String path) {
        response302Header(path, null);
    }

    public void redirectSuccessLogin(String path) {
        response302Header(path, "logined=true");
    }

    private void response200Header(int lengthOfBodyContent, String contentType) throws IOException {
        dos.writeBytes("HTTP/1.1 " + HttpStatus.OK.getCode() + " " + HttpStatus.OK.getMessage() + " \r\n");
        dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": " + contentType + "\r\n");
        dos.writeBytes(HttpHeader.CONTENT_LENGTH.getValue() + ": " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void response404Header(int lengthOfBodyContent) throws IOException {
        dos.writeBytes("HTTP/1.1 " + HttpStatus.NOT_FOUND.getCode() + " " + HttpStatus.NOT_FOUND.getMessage() + " \r\n");
        dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
        dos.writeBytes(HttpHeader.CONTENT_LENGTH.getValue() + ": " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void response302Header(String redirectPath, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getMessage() + " \r\n");
            dos.writeBytes(HttpHeader.LOCATION.getValue() + ": " + redirectPath + "\r\n");
            if (cookie != null) {
                dos.writeBytes(HttpHeader.SET_COOKIE.getValue() + ": " + cookie + "\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html;charset=utf-8";
        if (path.endsWith(".css")) return "text/css;charset=utf-8";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        return "application/octet-stream"; // 알 수 없는 경우 바이너리로 처리
    }

}
