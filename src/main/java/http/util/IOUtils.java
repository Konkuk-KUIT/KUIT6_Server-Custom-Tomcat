package http.util;

import java.io.BufferedReader;
import java.io.IOException;

public class IOUtils {
    /**
     *
     * @param br
     * socket으로부터 가져온 InputStream
     *
     * @param contentLength
     * 헤더의 Content-Length의 값이 들어와야한다.
     *
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    public static String getCookieValue(String cookies, String key) {
        if (cookies == null || key == null) return null;

        String[] pairs = cookies.split(";");
        for (String pair : pairs) {
            String[] kv = pair.trim().split("=", 2); // = 기준, 최대 2개
            if (kv.length == 2) {
                if (kv[0].trim().equals(key)) {
                    return kv[1].trim();
                }
            }
        }
        return null;
    }
}