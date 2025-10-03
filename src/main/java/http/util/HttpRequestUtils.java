package http.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


public class HttpRequestUtils {
    public static Map<String, String> parseQueryParameter(String queryString) {

        Map<String, String> result = new LinkedHashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return result;
        }

        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            if (pair == null || pair.isEmpty()) {
                continue;
            }

            String[] kv = pair.split("=", 2);
            String key = decodeUrlComponent(kv[0]);
            if (key.isEmpty()) {
                continue;
            }
            String value = kv.length > 1 ? decodeUrlComponent(kv[1]) : "";
            result.put(key, value);
        }
        return result;
    }

    private static String decodeUrlComponent(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return value;

        }
    }
}