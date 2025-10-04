package webserver;

import enums.HttpUrls;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;


public class HttpResponse {

    private DataOutputStream dos;
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void forward(String path) throws IOException {
        String fileUrl = HttpUrls.ROOT.getPath();
        byte[] body = Files.readAllBytes(Paths.get(fileUrl,path));
        if (path.endsWith(".css")) {
            responseCSS();
        } else {
            response200Header(dos, body.length);
            responseBody(dos, body);
        }
    }

    public void redirect(String path, boolean isCookie) throws IOException {
        response302Header(dos,path, isCookie);
    }

    private void responseCSS() throws IOException {
        try {
            String cssPath = HttpUrls.ROOT.getPath() + "/css/styles.css";
            byte[] body = Files.readAllBytes(Paths.get(cssPath));

            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body);
            dos.flush();

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String bodyContent, boolean isCookie) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location:" + bodyContent +"\r\n");
            if(isCookie) dos.writeBytes("Set-Cookie: logined=true" + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
