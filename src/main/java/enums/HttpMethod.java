package enums;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    TRACE("TRACE"),
    ;

    private final String value;

    HttpMethod(String method) {
        this.value = method;
    }

    public String getValue() {
        return value;
    }
}
