package webserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

public class HttpResponse {
    DataOutputStream dos;
    private HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public static HttpResponse from(DataOutputStream dos) {
        return new HttpResponse(dos);
    }

    public void response200Header(int lengthOfBodyContent) throws IOException {
        response200Header(lengthOfBodyContent, false);
    }

    public void response200Header(int lengthOfBodyContent, boolean isCss) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        if(isCss) {
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
        } else {
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        }
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    public void response302Header(String route) throws IOException {
        response302Header(route, false);
    }
    public void response302Header(String route, boolean hasCookie) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: " + route + " \r\n");
        if(hasCookie) {
            dos.writeBytes("Set-Cookie: logined=true \r\n");
        }
        dos.writeBytes("\r\n");
    }

    public void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
