package webserver.enums;
public enum HeaderName {
    HOST("Host"), CONTENT_TYPE("Content-Type"), CONTENT_LENGTH("Content-Length"),
    COOKIE("Cookie"), LOCATION("Location"), SET_COOKIE("Set-Cookie"),
    CONNECTION("Connection"), X_CONTENT_TYPE_OPTIONS("X-Content-Type-Options");
    public final String text; HeaderName(String t){ this.text=t; }
}