package http.enums;

import java.util.Locale;

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
        String normalized = filePath.split("\\?")[0].toLowerCase(Locale.ROOT);

        if (normalized.endsWith(".css")) {
            return TEXT_CSS;
        } else if (normalized.endsWith(".js")) {
            return APPLICATION_JAVASCRIPT;
        } else if (normalized.endsWith(".png")) {
            return IMAGE_PNG;
        } else if (normalized.endsWith(".jpg") || normalized.endsWith(".jpeg")) {
            return IMAGE_JPEG;
        } else {
            return TEXT_HTML;
        }
    }
}