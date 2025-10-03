package http.response;

import http.constant.ContentType;
import http.constant.HttpStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final OutputStream outputStream;
    private final Map<String, String> headers;
    private HttpStatus status;

    public HttpResponse(OutputStream outputStream) {
        this.outputStream = outputStream;
        this.headers = new HashMap<>();
        this.status = HttpStatus.OK;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void forward(String path) throws IOException {
        // webapp 폴더의 파일 읽기
        String filePath = "./webapp" + path;
        File file = new File(filePath);

        if (!file.exists() || file.isDirectory()) {
            sendNotFound();
            return;
        }

        byte[] body = Files.readAllBytes(file.toPath());
        ContentType contentType = ContentType.fromPath(path);

        // 헤더 설정
        addHeader("Content-Type", contentType.getValue());
        addHeader("Content-Length", String.valueOf(body.length));

        // 응답 전송
        sendResponse(body);
    }

    public void redirect(String path) throws IOException {
        setStatus(HttpStatus.FOUND);
        addHeader("Location", path);
        sendResponse(new byte[0]);
    }

    public void sendRedirectWithCookie(String path, String cookieName, String cookieValue) throws IOException {
        setStatus(HttpStatus.FOUND);
        addHeader("Location", path);
        addHeader("Set-Cookie", cookieName + "=" + cookieValue);
        sendResponse(new byte[0]);
    }

    private void sendResponse(byte[] body) throws IOException {
        DataOutputStream dos = new DataOutputStream(outputStream);

        // Status Line
        dos.writeBytes(status.getStatusLine() + " \r\n");

        // Headers
        for (Map.Entry<String, String> header : headers.entrySet()) {
            dos.writeBytes(header.getKey() + ": " + header.getValue() + "\r\n");
        }

        // 빈 줄
        dos.writeBytes("\r\n");

        // Body
        if (body.length > 0) {
            dos.write(body, 0, body.length);
        }

        dos.flush();
    }

    private void sendNotFound() throws IOException {
        setStatus(HttpStatus.NOT_FOUND);
        addHeader("Content-Type", "text/html;charset=utf-8");
        String body = "<h1>404 Not Found</h1>";
        sendResponse(body.getBytes());
    }
}