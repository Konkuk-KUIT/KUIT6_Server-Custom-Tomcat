package http;

public enum HttpMethod {
    GET("get"),
    POST("post");

    private final String method;

    public String getMethod() {
        return method;
    }

    HttpMethod(String method) {
        this.method = method;
    }
}
