package http;

import http.constant.HttpMethod;
import http.request.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private static final String TEST_DIR = "src/test/resources/http/";

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void POST_요청_파싱() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(TEST_DIR + "POST_signup.txt"));

        assertEquals(HttpMethod.POST, httpRequest.getMethod());
        assertEquals("/user/signup", httpRequest.getPath());
        assertEquals("userId=test&password=1234&name=tester&email=test@test.com", httpRequest.getBody());
        assertEquals(57, httpRequest.getHeaders().getContentLength());
    }

    @Test
    void GET_요청_파싱() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(TEST_DIR + "GET_index.txt"));

        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/index.html", httpRequest.getPath());
        assertEquals("", httpRequest.getBody());
    }

    @Test
    void GET_쿼리스트링_파싱() throws IOException {
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(TEST_DIR + "GET_with_query.txt"));

        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/user/signup", httpRequest.getPath());
        assertEquals("userId=test&password=1234", httpRequest.getQueryString());
    }
}