package http.enums;

public enum HttpMethod {
    GET("GET"),
    POST("POST");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public String getValue() {
        return method;
    }

    public static HttpMethod from(String method) {
        for (HttpMethod httpMethod : HttpMethod.values()) {
            if (httpMethod.getValue().equals(method)) {
                return httpMethod;
            }
        }
        // todo: Custom Exception
        throw new IllegalArgumentException("Unknown HTTP method: " + method);
    }
}