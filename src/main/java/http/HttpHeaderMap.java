package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpHeaderMap {
    private final Map<String, String> headers;

    public HttpHeaderMap(Map<String, String> headers) {
        this.headers = headers;
    }

    public static HttpHeaderMap parse(BufferedReader br) throws IOException {
        Map<String, String> map = new LinkedHashMap<>(); // 읽은 입력 순서 유지하도록 LinkedHashMap 사용
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(":"); // : 를 기준으로 나누어줌
            if (idx > 0) {
                String key = line.substring(0, idx).trim();
                String value = line.substring(idx + 1).trim();
                map.put(key, value);
            }
        }
        return new HttpHeaderMap(map);
    }

    public String getValue(String headerName) {
        for (Map.Entry<String, String> e : headers.entrySet()) {
            if (e.getKey().equalsIgnoreCase(headerName))
                return e.getValue();
        }
        return null;
    }

    public int getContentLength() {
        String length = getValue("Content-Length");
        return Integer.parseInt(length);
    }

    public String getCookie() {
        return getValue("Cookie");
    }

    public boolean hasCookie(String key, String value) {
        String cookieHeader = headers.get("Cookie");
        if(cookieHeader== null) return false;

        return Arrays.stream(cookieHeader.split(";"))// 쿠키 문자열을 ;을 기준으로
                .map(cookie -> cookie.trim().split("=")) // 각 원소를 = 로 key, value 구분
                .filter(kv -> kv.length == 2)
                .anyMatch(kv -> kv[0].trim().equals(key) && kv[1].trim().equals(value)); // 하나라도 매치되는게 잇으면 true 반환
    }

    public boolean isLogined() {
        return hasCookie("logined", "true");
    }

    public String get(String key) {
        return headers.get(key);
    }

}
