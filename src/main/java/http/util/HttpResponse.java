package http.util;

import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    DataOutputStream dos;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void forward(String filePath) {
        try {
            byte[] body = Files.readAllBytes(Paths.get(filePath));
            response200Header(body.length);
            dos.write(body, 0, body.length);
            dos.flush();

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response200Header(int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response302Header(String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    public void response200Css(int length) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + length + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    public void responseCss(String filePath) {
        try {
            byte[] body = Files.readAllBytes(Paths.get(filePath));
            response200Css(body.length);
            dos.write(body, 0, body.length);
            dos.flush();

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void response302Cookie(String location, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            if (cookie != null) {
                dos.writeBytes("Set-Cookie: " + cookie + "\r\n"); // 쿠키 설정
            }
            dos.writeBytes("Location: " + location + "\r\n"); // 리다이렉트 경로
            dos.writeBytes("Content-Length: 0\r\n");
            dos.writeBytes("Connection: close\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
