package http.constant;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH;

    public static HttpMethod from(String method) {
        try {
            return HttpMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GET; // 기본값
        }
    }
}