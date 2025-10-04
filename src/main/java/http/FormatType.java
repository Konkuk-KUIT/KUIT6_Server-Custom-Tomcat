package http;
import java.util.Set;

public enum FormatType {
    HTML("text/html; charset=utf-8", Set.of(".html", ".htm")),
    CSS("text/css",                  Set.of(".css")),
    JS("application/javascript; charset=utf-8", Set.of(".js")),
    JSON("application/json; charset=utf-8",     Set.of(".json")),
    SVG("image/svg+xml",             Set.of(".svg")),
    PNG("image/png",                 Set.of(".png")),
    JPEG("image/jpeg",               Set.of(".jpg", ".jpeg")),
    GIF("image/gif",                 Set.of(".gif")),
    ICO("image/x-icon",              Set.of(".ico")),
    OCTET("application/octet-stream",Set.of());

    private final String contentType;
    private final Set<String> exts;

    FormatType(String contentType, Set<String> exts) {
        this.contentType = contentType;
        this.exts = exts;
    }
    public String contentType() { return contentType; }

    public static String fromFilename(String name) {
        String n = name.toLowerCase();
        for (FormatType mt : values()) {
            for (String ext : mt.exts) {
                if (n.endsWith(ext)) return mt.contentType;
            }
        }
        return OCTET.contentType;
    }
}
