package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    public static Map<String, String> parseQueryString(String queryString) {
        if (queryString == null || queryString.isEmpty()) {
            return new HashMap<>();
        }
        return Arrays.stream(queryString.split("&"))
                .map(pair -> pair.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> parts[1],
                        (oldValue, newValue) -> newValue
                ));
    }

    public static Map<String, String> parseCookies(String cookieString) {
        if (cookieString == null || cookieString.isEmpty()) {
            return new HashMap<>();
        }
        return Arrays.stream(cookieString.split("; "))
                .map(pair -> pair.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> parts[1]
                ));
    }
}