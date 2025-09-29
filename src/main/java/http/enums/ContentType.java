package http.enums;

public enum ContentType {
    TEXT_HTML("text/html;charset=utf-8"),
    TEXT_CSS("text/css"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg");

    private final String mimeType;

    ContentType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getValue() {
        return mimeType;
    }

    public static ContentType fromFileExtension(String filePath) {
        if (filePath.endsWith(".css")) {
            return TEXT_CSS;
        } else if (filePath.endsWith(".js")) {
            return APPLICATION_JAVASCRIPT;
        } else if (filePath.endsWith(".png")) {
            return IMAGE_PNG;
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return IMAGE_JPEG;
        } else {
            return TEXT_HTML;
        }
    }
}