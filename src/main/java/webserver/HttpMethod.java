package webserver;

public enum HttpMethod {
    POST("POST"),
    GET("GET");

    private String method;

    HttpMethod(String method) {
        this.method = method;
    }

    public static HttpMethod fromString(String method) {
        return HttpMethod.valueOf(method.toLowerCase());
    }

    public String getMethod() {
        return method;
    }
}
