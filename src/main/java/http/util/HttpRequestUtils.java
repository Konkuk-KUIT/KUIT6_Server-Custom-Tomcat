package http.util;

import webserver.HttpRequest;
import webserver.enums.HttpMethod;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
    public static HttpRequest parse(BufferedReader br) throws IOException {
        HttpMethod method;
        String path;
        Map<String, String> headers = new HashMap<>();
        String body;

        // 1. 첫 줄: Method, Path, Version
        String line = br.readLine();
        if (line == null || line.isEmpty()) {
            throw new IOException("Empty Request");
        }

        String[] requestLine = line.split(" ");
        method = HttpMethod.fromValue(requestLine[0]);
        path = requestLine[1] ;

        // 2. 헤더 읽기
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(":");
            if (idx > 0) {
                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                headers.put(key, value);
            }
        }

        // 3. Content-Length 확인 후 body 읽기
        String contentLengthHeader = headers.get("Content-Length");
        int contentLength = 0;
        if (contentLengthHeader != null) {
            try {
                contentLength = Integer.parseInt(contentLengthHeader);
            } catch (NumberFormatException ignored) {}
        }

        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            int read = br.read(bodyChars, 0, contentLength);
            body = new String(bodyChars, 0, read);
        } else {
            body = "";
        }
        return HttpRequest.from(method,path,headers,body);
    }

    public static Map<String,String> parseCookies(String cookies) {
        if (cookies == null || cookies.isEmpty()) {
            return new HashMap<>();
        }

        return Arrays.stream(cookies.split(";"))
                .map(String::trim)
                .map(cookie -> cookie.split("=", 2))
                .filter(pair -> pair.length == 2)
                .collect(Collectors.toMap(
                        pair -> pair[0].trim(),
                        pair -> pair[1].trim()
                ));
    }

    public static Map<String, String> parseFormData(String formData) {
        Map<String, String> map = new HashMap<>();

        if (formData == null || formData.isEmpty()) {
            return map;
        }

        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2); // 2개만 split (value에 '=' 있을 수도 있음)
            String key = keyValue[0];
            String value = keyValue[1];
            map.put(key, value);
        }

        return map;
    }
}