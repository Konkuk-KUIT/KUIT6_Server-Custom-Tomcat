package constant;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static HttpMethod from(String method) {
        return HttpMethod.valueOf(method);
    }
}
