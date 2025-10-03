package test.java.http;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TestUtil {
    private TestUtil() {}

    public static BufferedReader bufferedReaderFromResource(String resourcePath) throws IOException {
        InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream(resourcePath);
        if (is == null) {
            throw new FileNotFoundException("Resource not found: " + resourcePath);
        }
        return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }
}
