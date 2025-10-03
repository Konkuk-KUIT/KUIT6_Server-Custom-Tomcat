package http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HttpResponse {
    private final DataOutputStream dos;

    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void forward(String path) throws IOException {
        File file = new File("webapp" + path);
        int fileLength = (int) file.length();
        if(path.equals("/user/login_failed.html")){
            responseLoginFailedHeader(dos, fileLength);
        }
        else response200Header(dos, fileLength);
        responseBody(dos, Files.readAllBytes(Paths.get(file.getPath())));
    }

    public void redirectLogin(String path) {
        responseLoginHeader(dos, "/index.html");
    }

    public void redirectLoginFail(String path) {
        response302Header(dos, "/user/login_failed.html");    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {

        }
    }

    private void responseLoginHeader(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true; Path=/; HttpOnly; SameSite=Lax\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {

        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path);
            dos.writeBytes("\r\n");
        } catch (IOException e) {
        }
    }

    private void responseLoginFailedHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {

        }
    }
}
