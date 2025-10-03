package http.util;

public enum httpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie");

    private final String value;

    httpHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
