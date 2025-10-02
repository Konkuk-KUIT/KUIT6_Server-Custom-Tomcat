package enums;

public enum HttpMethod {
    GET("GET"), POST("POST");

    private final String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public String getValue() {
        return method;
    }


    public static HttpMethod from(String method) {
        return switch (method.toUpperCase()) {
            case "GET" -> GET;
            case "POST" -> POST;
            default -> throw new IllegalArgumentException("Unknown HTTP method");
        };
    }
}
