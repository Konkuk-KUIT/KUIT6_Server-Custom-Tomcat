package http.response;

import http.constant.HttpStatus;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {

    private static final String TEST_DIR = "src/test/resources/http/response/";

    private OutputStream outputStreamToFile(String path) throws IOException {
        // 디렉토리가 없으면 생성
        Files.createDirectories(Paths.get(TEST_DIR));
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    void forward_테스트() throws IOException {
        String outputPath = TEST_DIR + "forward_output.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(outputPath));

        httpResponse.forward("/index.html");

        // 파일 내용 확인
        String content = Files.readString(Paths.get(outputPath));

        assertTrue(content.contains("HTTP/1.1 200 OK"));
        assertTrue(content.contains("Content-Type: text/html;charset=utf-8"));
        assertTrue(content.contains("Content-Length:"));
        assertTrue(content.contains("<!doctype html>"));  // index.html 내용의 일부
    }

    @Test
    void redirect_테스트() throws IOException {
        String outputPath = TEST_DIR + "redirect_output.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(outputPath));

        httpResponse.redirect("/index.html");

        // 파일 내용 확인
        String content = Files.readString(Paths.get(outputPath));

        assertTrue(content.contains("HTTP/1.1 302 Found"));
        assertTrue(content.contains("Location: /index.html"));
        assertFalse(content.contains("<!doctype html>"));  // Body 없음
    }

    @Test
    void redirect_with_cookie_테스트() throws IOException {
        String outputPath = TEST_DIR + "redirect_cookie_output.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(outputPath));

        httpResponse.sendRedirectWithCookie("/index.html", "logined", "true");

        // 파일 내용 확인
        String content = Files.readString(Paths.get(outputPath));

        assertTrue(content.contains("HTTP/1.1 302 Found"));
        assertTrue(content.contains("Location: /index.html"));
        assertTrue(content.contains("Set-Cookie: logined=true"));
    }

    @Test
    void CSS_파일_forward_테스트() throws IOException {
        String outputPath = TEST_DIR + "css_output.txt";
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(outputPath));

        httpResponse.forward("/css/styles.css");

        String content = Files.readString(Paths.get(outputPath));

        assertTrue(content.contains("HTTP/1.1 200 OK"));
        assertTrue(content.contains("Content-Type: text/css"));
    }
}