package webserver;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    CONTENT_TYPE("Content-Type");

    private final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public String getValue() {
        return headerName;
    }
}
