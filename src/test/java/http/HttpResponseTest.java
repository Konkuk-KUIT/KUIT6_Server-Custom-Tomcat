package http;
import http.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {
    private static final String TEST_DIR = "./build/test-output/";
    private static final String FORWARD_PATH = "forward_index.html";
    private static final String REDIRECT_PATH = "redirect_index.html";
    private static final String NOTFOUND_PATH = "notfound.html";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_DIR));
    }

    @AfterEach
    void cleanUp() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_DIR + FORWARD_PATH));
        Files.deleteIfExists(Paths.get(TEST_DIR + REDIRECT_PATH));
        Files.deleteIfExists(Paths.get(TEST_DIR + NOTFOUND_PATH));
    }

    private OutputStream outputStreamToFile(String path) throws IOException {
        return Files.newOutputStream(Paths.get(path));
    }

    @Test
    @DisplayName("forward: index.html 파일을 응답으로 내려준다")
    void forward_index_html() throws IOException {
        // given
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(TEST_DIR + FORWARD_PATH));

        // when
        httpResponse.forward("/index.html");

        // then
        String result = Files.readString(Path.of(TEST_DIR + FORWARD_PATH));
        assertThat(result).contains("HTTP/1.1 200 OK")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("<!doctype html");
    }

    @Test
    @DisplayName("redirect: 302 Found 응답을 내려준다")
    void redirect_to_index() throws IOException {
        // given
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(TEST_DIR + REDIRECT_PATH));

        // when
        httpResponse.sendRedirect("/index.html");

        // then
        String result = Files.readString(Path.of(TEST_DIR + REDIRECT_PATH));
        assertThat(result).contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    @DisplayName("notfound: 404 Not Found 응답을 내려준다")
    void notfound_response() throws IOException {
        // given
        HttpResponse httpResponse = new HttpResponse(outputStreamToFile(TEST_DIR + NOTFOUND_PATH));

        // when
        httpResponse.sendNotFound();

        // then
        String result = Files.readString(Path.of(TEST_DIR + NOTFOUND_PATH));
        assertThat(result).contains("HTTP/1.1 404 Not Found")
                .contains("<h1>404 Not Found</h1>");
    }
}