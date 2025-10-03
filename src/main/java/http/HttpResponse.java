package http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final DataOutputStream dos;
    private final Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream out) {
        this.dos = new DataOutputStream(out);
    }

    public void forward(String path) throws IOException {
        File file = new File("webapp" + path);
        if (!file.exists()) {
            sendError(404, "Not Found", "<h1>404 Not Found</h1>");
            return;
        }

        byte[] body = readFile(file);
        addHeader("Content-Type", contentType(path));
        addHeader("Content-Length", String.valueOf(body.length));

        writeStatusLine(200, "OK");
        writeHeaders();
        writeBody(body);
    }

    public void sendRedirect(String path) throws IOException {
        writeStatusLine(302, "Found");
        addHeader("Location", path);
        writeHeaders();
    }

    public void sendRedirectWithCookie(String path, String cookie) throws IOException {
        writeStatusLine(302, "Found");
        addHeader("Location", path);
        addHeader("Set-Cookie", cookie);
        writeHeaders();
    }

    private void writeStatusLine(int statusCode, String message) throws IOException {
        dos.writeBytes("HTTP/1.1 " + statusCode + " " + message + "\r\n");
    }

    private void writeHeaders() throws IOException {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            dos.writeBytes(entry.getKey() + ": " + entry.getValue() + "\r\n");
        }
        dos.writeBytes("\r\n");
    }

    private void writeBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }

    private void sendError(int code, String message, String body) throws IOException {
        byte[] bytes = body.getBytes();
        writeStatusLine(code, message);
        addHeader("Content-Type", "text/html;charset=utf-8");
        addHeader("Content-Length", String.valueOf(bytes.length));
        writeHeaders();
        writeBody(bytes);
    }

    private void addHeader(String key, String value) {
        headers.put(key, value);
    }

    private byte[] readFile(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            return fis.readAllBytes();
        }
    }

    private String contentType(String path) {
        if (path.endsWith(".css")) return "text/css;charset=utf-8";
        if (path.endsWith(".js")) return "application/javascript;charset=utf-8";
        if (path.endsWith(".html")) return "text/html;charset=utf-8";
        return "application/octet-stream";
    }
}
