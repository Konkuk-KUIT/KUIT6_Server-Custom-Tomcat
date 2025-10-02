package http.constant;

public enum ContentType {
    HTML("text/html;charset=utf-8"),
    CSS("text/css"),
    JAVASCRIPT("application/javascript"),
    PNG("image/png"),
    JPEG("image/jpeg"),
    GIF("image/gif");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContentType fromPath(String path) {
        if (path.endsWith(".css")) {
            return CSS;
        } else if (path.endsWith(".js")) {
            return JAVASCRIPT;
        } else if (path.endsWith(".png")) {
            return PNG;
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return JPEG;
        } else if (path.endsWith(".gif")) {
            return GIF;
        } else {
            return HTML;
        }
    }
}