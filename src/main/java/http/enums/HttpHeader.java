package main.java.http.enums;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    CONTENT_TYPE("Content-Type");

    private final String key;

    HttpHeader(String key) { this.key = key; }
    public String getKey() { return key; }
}
