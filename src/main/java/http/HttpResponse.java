package http;

import http.constants.HttpHeader;
import http.constants.HttpStatusCode;
import webserver.RequestHandler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

//    private String httpVersion;
//    private String httpContentType;
//    private String httpContentLength;
//    private String httpBody;
    private DataOutputStream dos;

    private HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public static HttpResponse from(DataOutputStream dos) {
        return new HttpResponse(dos);
    }

    public void forward(String contentType, String url) {
        try {
            dos.writeBytes(HttpHeader.HTTP_VERSION +" "+ HttpStatusCode.SUCCESS+ "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE + ": "+ contentType + "\r\n");
//            dos.writeBytes(HttpHeader.CONTENT_LENGTH + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");

            responseBody(dos, readFile(url));

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirect(String path, String setCookie, String url) {
        try {
            dos.writeBytes(HttpHeader.HTTP_VERSION + " "+ HttpStatusCode.REDIRECT + "\r\n");
            dos.writeBytes(HttpHeader.LOCATION + ": " + path + "\r\n");
            if (setCookie != null && !setCookie.isEmpty()) {
                dos.writeBytes(HttpHeader.SET_COOKIE + ": " + setCookie + "\r\n");
            }
            dos.writeBytes("\r\n");

            responseBody(dos, readFile(url));
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private byte[] readFile(String url) {
        Path filePath = Paths.get("./webapp" + url);
        byte[] body = null;

        try {

            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                // 파일이 존재하면 파일 내용을 읽어옵니다.
                body = Files.readAllBytes(filePath);
            } else {
                // 파일이 존재하지 않으면 404 Not Found 응답을 보냅니다.
                body = HttpStatusCode.CLIENT_ERROR.toString().getBytes();
                // 404 Not Found 응답 헤더를 작성합니다.
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return body;

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
