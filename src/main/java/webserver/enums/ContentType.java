package webserver.enums;
public enum ContentType {
    HTML("text/html; charset=UTF-8"),
    CSS("text/css; charset=UTF-8"),
    JS("application/javascript; charset=UTF-8"),
    PNG("image/png"), JPG("image/jpeg"), GIF("image/gif"),
    SVG("image/svg+xml"), JSON("application/json; charset=UTF-8"),
    OCTET("application/octet-stream");
    public final String v; ContentType(String v){ this.v=v; }
}