package enums;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"),
    LOCATION("Location"),
    SET_COOKIE("Set-Cookie"),
    CONTENT_TYPE("Content-Type");

    private final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public boolean matches(String line) {
        return line.startsWith(value);
    }
}