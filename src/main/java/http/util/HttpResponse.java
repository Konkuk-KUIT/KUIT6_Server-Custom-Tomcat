package http.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private final DataOutputStream dos;
    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    /**
     * 지정된 경로의 정적 파일을 찾아 200 OK 응답을 보냅니다.
     * @param path webapp 폴더를 기준으로 한 파일 경로 (e.g., "/index.html")
     */
    public void forward(String path) {
        try {
            byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());

            if (path.endsWith(".css")) {
                headers.put(httpHeader.CONTENT_TYPE.getValue(), ContentType.CSS.getValue());
            } else {
                headers.put(httpHeader.CONTENT_TYPE.getValue(), ContentType.HTML.getValue());
            }
            headers.put(httpHeader.CONTENT_LENGTH.getValue(), String.valueOf(body.length));

            response200Header();
            responseBody(body);
        } catch (IOException e) {
            response404Header();
        }
    }

    /**
     * 지정된 경로로 302 Redirect 응답을 보냅니다.
     * @param path 리다이렉트할 경로 (e.g., "/index.html")
     */
    public void redirect(String path) {
        try {
            dos.writeBytes(httpStatus.FOUND.getStatusLine());
            dos.writeBytes(httpHeader.LOCATION.getValue() + ": " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * 로그인 성공 시, 쿠키를 포함한 302 Redirect 응답을 보냅니다.
     * @param path 리다이렉트할 경로
     */
    public void sendLoginSuccessRedirect(String path) {
        try {
            dos.writeBytes(httpStatus.FOUND.getStatusLine());
            dos.writeBytes(httpHeader.LOCATION.getValue() + ": " + path + "\r\n");
            dos.writeBytes(httpHeader.SET_COOKIE.getValue() + ": logined=true; Path=/\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header() {
        try {
            dos.writeBytes(httpStatus.OK.getStatusLine());
            processHeaders();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header() {
        try {
            byte[] body = "404 Not Found".getBytes();
            dos.writeBytes(httpStatus.NOT_FOUND.getStatusLine());
            dos.writeBytes(httpHeader.CONTENT_TYPE.getValue() + ": " + ContentType.HTML.getValue() + "\r\n");
            dos.writeBytes(httpHeader.CONTENT_LENGTH.getValue() + ": " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            responseBody(body);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void processHeaders() throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        dos.writeBytes("\r\n");
    }
}
