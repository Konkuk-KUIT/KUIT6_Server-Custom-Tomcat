package webserver;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public boolean isEqual(String method) {
        return this.name().equalsIgnoreCase(method);
    }
}
