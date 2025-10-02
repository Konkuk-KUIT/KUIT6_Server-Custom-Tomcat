package webserver.enums;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");

    private final String value;

    HttpMethod(String value) {
        this.value = value;
    }

    public static HttpMethod fromValue(String value) {
        for (HttpMethod method : HttpMethod.values()) {
            if (method.value.equalsIgnoreCase(value)) { // 대소문자 구분 없이
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown method: " + value);
    }

}
