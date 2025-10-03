package enumclasses;

public enum HttpHeader {
    CONTENT_LENGTH("Content-Length"),
    CONTENT_TYPE("Content-Type"),
    SET_COOKIE("Set-Cookie"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    EMPTY("");
    public final String text;
    HttpHeader(String t) {
        this.text=t;
    }
}
