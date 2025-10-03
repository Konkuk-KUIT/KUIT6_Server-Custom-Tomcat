package webserver;

import enums.HttpHeader;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {

    DataOutputStream dos;
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());


    HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    //원하는 html 파일을 보여주는 메서드
    public void forward(String path) throws IOException {

        File file = new File("./webapp"+path);

        if(file.exists()) { //존재한다면 파일 출력
            byte[] body = java.nio.file.Files.readAllBytes(file.toPath());
            response200Header(dos, body.length, getContentType(path));
            responseBody(dos, body);
        } else { //존재하지 않는다면 404 not found 출력
            byte[] body = "<h1>404 Not Found</h1>".getBytes();
            response404Header(dos, body.length);
            responseBody(dos, body);
        }
    }

    //redirect 시켜주는 메서드
    public void redirect(String path, String cookie) throws IOException {

        response302Header(dos, path, cookie);

    }

    private String getContentType(String path) {
        if(path.endsWith(".html")) {
            return "text/html";
        }
        if(path.endsWith(".css")) {
            return "text/css";
        }
        if(path.endsWith(".js")) {
            return "application/javascript";
        }
        //기본값 : 알 수 없는 파일
        return "application/octet-stream";
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.value() + ": " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes( HttpHeader.CONTENT_LENGTH.value() + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectUrl, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes(HttpHeader.LOCATION.value() + ": " + redirectUrl + "\r\n");
            //쿠키가 있다면 헤더 추가
            if(cookie != null) { dos.writeBytes(HttpHeader.SET_COOKIE.value() + ": " + cookie + "\r\n"); }
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.value() + ": 0\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.value() + ": text/html;charset=utf-8\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.value() + ": " + lengthOfBodyContent + "\r\n");
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
