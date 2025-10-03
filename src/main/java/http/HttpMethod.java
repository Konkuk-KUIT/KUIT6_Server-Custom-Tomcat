package http;

public enum HttpMethod {
    GET,
    POST;

    public boolean isEqual(HttpMethod method) {
        return this == method;
    }

    public boolean isEqual(String rawMethod) {
        return rawMethod != null && name().equalsIgnoreCase(rawMethod);
    }
}
