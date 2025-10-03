package http.util;

import java.util.Locale;

public class MimeTypes {
    // 인스턴스화 방지를 위한 기본 생성자
    private MimeTypes() {}

    public static String fromFilename(String filename) {
        String f = filename.toLowerCase(Locale.ROOT);

        if (f.endsWith(".html") || f.endsWith(".htm")) return "text/html; charset=utf-8";
        if (f.endsWith(".css"))  return "text/css; charset=utf-8";
        if (f.endsWith(".js"))   return "application/javascript; charset=utf-8";
        if (f.endsWith(".json")) return "application/json; charset=utf-8";
        if (f.endsWith(".svg"))  return "image/svg+xml";
        if (f.endsWith(".png"))  return "image/png";
        if (f.endsWith(".jpg") || f.endsWith(".jpeg")) return "image/jpeg";
        if (f.endsWith(".gif"))  return "image/gif";
        if (f.endsWith(".ico"))  return "image/x-icon";
        if (f.endsWith(".webp")) return "image/webp";
        if (f.endsWith(".woff")) return "font/woff";
        if (f.endsWith(".woff2")) return "font/woff2";
        if (f.endsWith(".ttf"))  return "font/ttf";
        if (f.endsWith(".wasm")) return "application/wasm";

        // 기본값
        return "text/html; charset=utf-8";
    }
}
