package http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {

    @Test
    @DisplayName("forward는 정적 파일을 읽어 200 응답을 생성한다")
    void forwardWritesStaticFile() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(out);

        response.forward("index.html");

        String raw = out.toString(StandardCharsets.UTF_8);
        String[] sections = raw.split("\r?\n\r?\n", 2);
        assertEquals(2, sections.length, "응답은 헤더와 바디를 포함해야 한다");

        String statusAndHeaders = sections[0];
        String body = sections[1];

        String[] headLines = statusAndHeaders.split("\r?\n");
        assertTrue(headLines[0].startsWith("HTTP/1.1 200"));

        Map<String, String> headerMap = toHeaderMap(headLines);
        assertEquals("text/html", headerMap.get(HttpHeader.CONTENT_TYPE.value()));

        String expectedBody = Files.readString(Path.of("webapp", "index.html"), StandardCharsets.UTF_8);
        assertEquals(expectedBody, body);
        assertEquals(String.valueOf(expectedBody.getBytes(StandardCharsets.UTF_8).length), headerMap.get(HttpHeader.CONTENT_LENGTH.value()));
    }

    @Test
    @DisplayName("redirect는 302 상태와 Location 헤더만 기록한다")
    void redirectWritesStatusAndLocation() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(out);

        response.response302Header("/index.html");

        String raw = out.toString(StandardCharsets.UTF_8);
        String[] sections = raw.split("\r?\n\r?\n", 2);
        assertEquals(2, sections.length);

        String statusAndHeaders = sections[0];
        String body = sections[1];

        String[] headLines = statusAndHeaders.split("\r?\n");
        assertEquals("HTTP/1.1 302 Found", headLines[0]);

        Map<String, String> headerMap = toHeaderMap(headLines);
        assertEquals("/index.html", headerMap.get(HttpHeader.LOCATION.value()));
        assertFalse(headerMap.containsKey(HttpHeader.CONTENT_LENGTH.value()));
        assertTrue(body.isEmpty());
    }

    private Map<String, String> toHeaderMap(String[] headLines) {
        Map<String, String> headerMap = new LinkedHashMap<>();
        for (int i = 1; i < headLines.length; i++) {
            String line = headLines[i];
            int idx = line.indexOf(": ");
            if (idx > 0) {
                headerMap.put(line.substring(0, idx), line.substring(idx + 2));
            }
        }
        return headerMap;
    }
}