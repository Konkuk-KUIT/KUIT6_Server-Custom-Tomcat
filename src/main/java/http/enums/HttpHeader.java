package http.enums;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    HOST("Host"),
    USER_AGENT("User-Agent");

    private final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public String getValue() {
        return headerName;
    }

    public static HttpHeader from(String headerName) {
        for (HttpHeader header : values()) {
            if (header.headerName.equals(headerName)) {
                return header;
            }
        }
        return null; // 알려지지 않은 헤더는 null return
    }
}