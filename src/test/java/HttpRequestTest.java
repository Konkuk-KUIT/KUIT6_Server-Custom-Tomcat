import http.HttpRequest;
import http.enums.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class HttpRequestTest {
    
    private static final String TEST_DIRECTORY = "src/test/resources/";

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    @DisplayName("POST 요청을 파싱할 때 메서드, URL, 헤더, 바디가 올바르게 파싱되어야 한다")
    public void parsePostRequestWithBodyAndHeaders() throws IOException {
        // given
        BufferedReader br = bufferedReaderFromFile(TEST_DIRECTORY + "request/post_request.txt");
        
        // when
        HttpRequest httpRequest = HttpRequest.from(br);
        
        // then
        assertEquals(HttpMethod.POST, httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getUrl());
        assertEquals("HTTP/1.1", httpRequest.getVersion());
        assertEquals("localhost:8080", httpRequest.getHeader("Host"));
        assertEquals(35, httpRequest.getContentLength());
        assertNotNull(httpRequest.getBody());
        assertTrue(httpRequest.getBody().contains("userId"));
    }

    @Test
    @DisplayName("GET 요청을 파싱할 때 쿼리 파라미터와 쿠키가 올바르게 파싱되어야 한다")
    public void parseGetRequestWithQueryParametersAndCookies() throws IOException {
        // given
        BufferedReader br = bufferedReaderFromFile(TEST_DIRECTORY + "request/get_request.txt");
        
        // when
        HttpRequest httpRequest = HttpRequest.from(br);
        
        // then
        assertEquals(HttpMethod.GET, httpRequest.getMethod());
        assertEquals("/index.html?name=foden&age=26", httpRequest.getUrl());
        assertEquals("name=foden&age=26", httpRequest.getQueryString());
        assertEquals("HTTP/1.1", httpRequest.getVersion());
        assertEquals("localhost:8080", httpRequest.getHeader("Host"));
        assertEquals("logined=true", httpRequest.getCookie());
        assertNull(httpRequest.getBody());
    }

    @Test
    @DisplayName("잘못된 형식의 요청 라인이 주어질 때 예외가 발생해야 한다")
    public void throwExceptionWhenInvalidRequestLineFormat() {
        // given
        BufferedReader br = new BufferedReader(new java.io.StringReader("INVALID REQUEST"));
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> HttpRequest.from(br));
    }

    @Test
    @DisplayName("빈 요청 라인이 주어질 때 예외가 발생해야 한다")
    public void throwExceptionWhenEmptyRequestLine() {
        // given
        BufferedReader br = new BufferedReader(new java.io.StringReader(""));
        
        // when & then
        assertThrows(IllegalArgumentException.class, () -> HttpRequest.from(br));
    }
}