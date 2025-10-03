package http;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());

    private final OutputStream out;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private HttpStatus status = HttpStatus.OK;

    public HttpResponse(OutputStream out) {
        this.out = out;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addHeader(HttpHeader header, String value) {
        addHeader(header.value(), value);
    }

    /** Forward a static file under ./webapp */
    public void forward(String path) throws IOException {
        String resourcePath = (path == null) ? "" : path;
        if (resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }

        if (resourcePath.isEmpty()) {
            send404(path);
            return;
        }

        File webRoot = new File("./webapp").getCanonicalFile();
        File target = new File(webRoot, resourcePath).getCanonicalFile();
        if (!target.getPath().startsWith(webRoot.getPath()) || !target.exists() || target.isDirectory()) {
            send404(resourcePath);
            return;
        }

        String contentType = Files.probeContentType(target.toPath());
        if (contentType == null) {
            contentType = guessContentType(resourcePath);
        }

        byte[] body = readAllBytes(target);
        status = HttpStatus.OK;
        headers.clear();
        addHeader(HttpHeader.CONTENT_TYPE, contentType);
        addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(body.length));
        writeHead();
        writeBody(body);
    }

    public void response302Header(String location) throws IOException {
        status = HttpStatus.FOUND;
        addHeader(HttpHeader.LOCATION, location);
        headers.entrySet().removeIf(entry -> {
            String key = entry.getKey();
            return !(HttpHeader.LOCATION.value().equalsIgnoreCase(key) || HttpHeader.SET_COOKIE.value().equalsIgnoreCase(key));
        });
        log.log(Level.INFO, "Send redirect to {0}", location);
        writeHead();
    }

    public void sendRedirect(String location) {
        try {
            response302Header(location);
            // Redirect 응답은 보통 바디를 비웁니다.
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void send404(String path) throws IOException {
        status = HttpStatus.NOT_FOUND;
        headers.clear();
        String notFound = "<h1>404 Not Found</h1>";
        byte[] body = notFound.getBytes();
        addHeader(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
        addHeader(HttpHeader.CONTENT_LENGTH, String.valueOf(body.length));
        log.log(Level.INFO, "Send 404 response path={0}", path);
        writeHead();
        writeBody(body);
    }

    private void writeHead() throws IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
        writer.write("HTTP/1.1 " + status.code() + " " + status.reason() + "\r\n");
        for (Map.Entry<String, String> h : headers.entrySet()) {
            writer.write(h.getKey() + ": " + h.getValue() + "\r\n");
        }
        writer.write("\r\n");
        writer.flush();
    }

    private void writeBody(byte[] body) throws IOException {
        out.write(body);
        out.flush();
    }

    private static String guessContentType(String path) {
        String p = path.toLowerCase();
        if (p.endsWith(".html") || p.endsWith(".htm")) return "text/html;charset=utf-8";
        if (p.endsWith(".css")) return "text/css";
        if (p.endsWith(".js")) return "application/javascript";
        if (p.endsWith(".png")) return "image/png";
        if (p.endsWith(".jpg") || p.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream";
    }

    private static byte[] readAllBytes(File f) throws IOException {
        try (FileInputStream fis = new FileInputStream(f)) {
            byte[] data = new byte[(int) f.length()];
            int read = fis.read(data);
            if (read != data.length) throw new IOException("Incomplete file read");
            return data;
        }
    }
}