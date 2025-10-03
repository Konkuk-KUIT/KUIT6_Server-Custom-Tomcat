package http.constants;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    HTTP_VERSION("HTTP/1.1");

    private final String text;
    HttpHeader(String text) { this.text = text; }
    @Override public String toString() { return text; }
}
