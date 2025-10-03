package http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private BufferedReader bufferedReaderFromFile(String relativePath) throws IOException {
        Path path = Path.of("src", "test", "resources").resolve(relativePath);
        return new BufferedReader(new InputStreamReader(Files.newInputStream(path), StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("POST 요청은 본문과 폼 파라미터를 파싱한다")
    void parsePostRequest() throws IOException {
        try (BufferedReader reader = bufferedReaderFromFile("http/post-signup.txt")) {
            HttpRequest request = HttpRequest.from(reader);

            assertEquals(HttpMethod.POST, request.getMethod());
            assertEquals("/user/signup", request.getPath());
            assertEquals("application/x-www-form-urlencoded", request.getHeader(HttpHeader.CONTENT_TYPE));
            assertEquals("abc123", request.getCookie("sessionId"));
            assertEquals("josh", request.getParameter("userId"));
            assertEquals("secret", request.getParameter("password"));
            assertTrue(request.getBody().contains("email=josh%40example.com"));
        }
    }

    @Test
    @DisplayName("GET 요청은 쿼리 파라미터를 파싱한다")
    void parseGetRequest() throws IOException {
        try (BufferedReader reader = bufferedReaderFromFile("http/get-user-list.txt")) {
            HttpRequest request = HttpRequest.from(reader);

            assertEquals(HttpMethod.GET, request.getMethod());
            assertEquals("/user/list.html", request.getPath());
            assertEquals("10", request.getParameter("offset"));
            assertEquals("5", request.getParameter("limit"));
            assertEquals("", request.getBody());
        }
    }
}