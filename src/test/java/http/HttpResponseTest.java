package test.java.http;
import main.java.http.HttpResponse;
import main.java.http.enums.HttpHeader;
import main.java.http.enums.HttpStatus;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

public class HttpResponseTest {
    private static final Path WEBAPP_DIR = Paths.get("webapp");
    private static final Path BUILD_DIR  = Paths.get("build/test-httpresponse");

    @BeforeEach
    void setUp() throws Exception {
        Files.createDirectories(WEBAPP_DIR);
        Files.createDirectories(BUILD_DIR);
    }

    private OutputStream outputStreamToFile(String name) throws IOException {
        Path p = BUILD_DIR.resolve(name);
        Files.createDirectories(p.getParent());
        return Files.newOutputStream(p);
    }

    private String readFile(String name) throws IOException {
        return Files.readString(BUILD_DIR.resolve(name), UTF_8);
    }

    @Test
    void forward_ok_writes_status_headers_and_body() throws Exception {
        String body = "HELLO";
        Files.writeString(WEBAPP_DIR.resolve("index_test.html"), body, UTF_8);

        try (OutputStream out = outputStreamToFile("resp_forward_ok.txt")) {
            HttpResponse resp = new HttpResponse(out);
            resp.forward("/index.html");
        }

        String raw = readFile("resp_forward_ok.txt");
        assertThat(raw).startsWith(HttpStatus.OK.format());
        assertThat(raw).contains(HttpHeader.CONTENT_TYPE.getKey() + ": text/html");
        assertThat(raw).contains(HttpHeader.CONTENT_LENGTH.getKey() + ": 5");
        assertThat(raw).endsWith(body);
    }

    @Test
    void forward_missing_writes_404() throws Exception {
        try (OutputStream out = outputStreamToFile("resp_forward_404.txt")) {
            HttpResponse resp = new HttpResponse(out);
            resp.forward("/no_such_file.html");
        }

        String raw = readFile("resp_forward_404.txt");
        assertThat(raw).startsWith(HttpStatus.NOT_FOUND.format());
        assertThat(raw).contains(HttpHeader.CONTENT_TYPE.getKey());
    }

    @Test
    void redirect_writes_302_and_location() throws Exception {
        try (OutputStream out = outputStreamToFile("resp_redirect.txt")) {
            HttpResponse resp = new HttpResponse(out);
            resp.redirect("/index.html");
        }

        String raw = readFile("resp_redirect.txt");
        assertThat(raw).startsWith(HttpStatus.FOUND.format());
        assertThat(raw).contains(HttpHeader.LOCATION.getKey() + ": /index.html");
        assertThat(raw).contains(HttpHeader.CONTENT_LENGTH.getKey() + ": 0");
    }

    @Test
    void redirect_with_cookie_sets_set_cookie_header() throws Exception {
        try (OutputStream out = outputStreamToFile("resp_redirect_cookie.txt")) {
            HttpResponse resp = new HttpResponse(out);
            resp.redirectWithCookie("/index.html", "logined=true; Path=/");
        }

        String raw = readFile("resp_redirect_cookie.txt");
        assertThat(raw).startsWith(HttpStatus.FOUND.format());
        assertThat(raw).contains(HttpHeader.LOCATION.getKey() + ": /index.html");
        assertThat(raw).contains(HttpHeader.SET_COOKIE.getKey() + ": logined=true; Path=/");
        assertThat(raw).contains(HttpHeader.CONTENT_LENGTH.getKey() + ": 0");
    }
}

