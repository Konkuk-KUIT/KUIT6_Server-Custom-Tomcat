package http.constants;

public enum HttpMethod {
    GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH;
    
    public static HttpMethod from(String method) {
        return valueOf(method.toUpperCase());
    }
}
