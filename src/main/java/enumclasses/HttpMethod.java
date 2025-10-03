package enumclasses;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS;

    public static HttpMethod from(String s) {
        return HttpMethod.valueOf(s.toUpperCase());
    }
}
