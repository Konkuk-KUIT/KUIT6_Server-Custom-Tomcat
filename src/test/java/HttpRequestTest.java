import http.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {

    private BufferedReader bufferedReaderFromFile(String path) throws IOException {
        return new BufferedReader(new InputStreamReader(Files.newInputStream(Paths.get(path))));
    }

    @Test
    @DisplayName("Http Request Test")
    void testHttpRequest() {
        try {
            HttpRequest httpRequest = HttpRequest.from(bufferedReaderFromFile("C:\\Users\\window10\\IdeaProjects\\KUIT6_Server-Custom-Tomcat\\src\\test\\resources\\MockHttpRequestMessage.txt"));
            assertEquals("/user/create", httpRequest.getUrl());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
