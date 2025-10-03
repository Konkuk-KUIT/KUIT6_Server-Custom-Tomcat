package http.util;

public enum HttpMethod {
    GET, POST;

    public static HttpMethod from(String method) {
        for (HttpMethod m : values()) {
            if (m.name().equalsIgnoreCase(method)) {
                return m;
            }
        }
        throw new IllegalArgumentException("Unknown method: " + method);
    }
}
