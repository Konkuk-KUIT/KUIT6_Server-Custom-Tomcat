package http.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestUtils {
//    public static Map<String, String> parseQueryParameter(String queryString) {
//        try {
//            String[] queryStrings = queryString.split("&");
//
//            return Arrays.stream(queryStrings)
//                    .map(q -> q.split("="))
//                    .collect(Collectors.toMap(queries -> queries[0], queries -> queries[1]));
//        } catch (Exception e) {
//            return new HashMap<>();
//        }
//    }
    public static Map<String, String> parseRequestLine(String requestLine) {
        Map<String, String> result = new HashMap<>();

        String[] parts = requestLine.split(" ");
        String method = parts[0];
        String pathAndQuery = parts[1];

        // 2. 경로와 쿼리 분리
        String path;
        String queryString = null;
        if (pathAndQuery.contains("?")) {
            String[] pathParts = pathAndQuery.split("\\?", 2);
            path = pathParts[0];         // "/hello"
            queryString = pathParts[1];  // "hello=123&min=123"
        } else {
            path = pathAndQuery;
        }

        // 결과 Map에 담기
        result.put("method", method);
        result.put("path", path);
        result.put("queryString", queryString);

        return result;
    }

    public static Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> queryMap = new HashMap<>();

        if (queryString == null || queryString.isEmpty()) {
            return queryMap; // 빈 맵 반환
        }

        // & 기준으로 분리 → key=value 형태
        String[] params = queryString.split("&");
        for (String param : params) {
            String[] keyValue = param.split("=", 2); // =를 기준으로 2개만 split
            String key = keyValue[0];
            String value = keyValue.length > 1 ? keyValue[1] : ""; // 값이 없으면 빈 문자열
            queryMap.put(key, value);
        }

        return queryMap;
    }
}