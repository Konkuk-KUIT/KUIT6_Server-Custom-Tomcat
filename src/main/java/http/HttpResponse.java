package http;

import constant.HttpStatusCode;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Logger;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private final DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void response302WithCookie(String location, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatusCode.FOUND.getMessage() + "\r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void response302Header(String location) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatusCode.FOUND.getMessage() + "\r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void response200(String path) {
        try {
            File file = new File("./webapp" + path);
            if (!file.exists()) {
                response404Header();
                return;
            }

            byte[] body = Files.readAllBytes(file.toPath());

            dos.writeBytes("HTTP/1.1 " + HttpStatusCode.OK.getMessage() + "\r\n");
            if (path.endsWith(".css")) {
                dos.writeBytes("Content-Type: text/css\r\n");
            } else {
                dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            }
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");

            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void response404Header() {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatusCode.NOT_FOUND.getMessage() + "\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes("<h1>404 Not Found</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
