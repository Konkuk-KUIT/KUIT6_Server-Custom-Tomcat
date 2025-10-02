package main.java.http.enums;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static HttpMethod from(String method) {
        for (HttpMethod m : values()) {
            if (m.name().equalsIgnoreCase(method)) return m;
        }
        throw new IllegalArgumentException("Unsupported method: " + method);
    }
}

