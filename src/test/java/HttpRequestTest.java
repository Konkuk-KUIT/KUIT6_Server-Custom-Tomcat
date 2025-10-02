import enums.HttpMethod;
import http.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    void testHttpRequestParsing() throws IOException {
        // given
        String testFile = "src/test/resources/http_request_example";

        // when
        HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile(testFile));

        // then
        assertEquals(HttpMethod.POST, httpRequest.getStartLine().getMethod());
        assertEquals("/user/create", httpRequest.getStartLine().getPath());
        assertEquals("localhost:8080", httpRequest.getHeader().get("Host"));
        assertEquals(40, httpRequest.getHeader().getContentLength());

        Map<String, String> params = httpRequest.getBody().getFormData();
        assertEquals("jw", params.get("userId"));
        assertEquals("password", params.get("password"));
        assertEquals("jungwoo", params.get("name"));
    }
}