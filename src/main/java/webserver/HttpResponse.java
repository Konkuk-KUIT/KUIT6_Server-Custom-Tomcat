package webserver;

import webserver.enums.ContentType;
import webserver.enums.HeaderName;
import webserver.enums.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;

public class HttpResponse {
    private HttpStatus status = HttpStatus.OK;
    private String contentType = ContentType.HTML.v;
    private byte[] body = new byte[0];

    public HttpResponse status(HttpStatus s){ this.status = s; return this; }
    public HttpResponse contentType(String ct){ this.contentType = ct; return this; }
    public HttpResponse body(byte[] b){ this.body = b; return this; }

    public void forward(OutputStream out, String webRoot, String path) throws IOException {
        if ("/".equals(path)) path = "/index.html";
        if (path.contains("..")) { writeText(out, HttpStatus.FORBIDDEN, "<h1>403</h1>"); return; }

        Path file = Path.of(webRoot + path);
        if (!Files.exists(file) || Files.isDirectory(file)) { writeText(out, HttpStatus.NOT_FOUND, "<h1>404</h1>"); return; }

        byte[] bytes = Files.readAllBytes(file);
        String ct = guess(path);
        write(out, HttpStatus.OK, ct, bytes, null);
    }

    public void redirect(OutputStream out, String location, String setCookie) throws IOException {
        write(out, HttpStatus.FOUND, null, new byte[0], new HeaderPair[] {
                new HeaderPair(HeaderName.LOCATION.text, location),
                (setCookie==null? null : new HeaderPair(HeaderName.SET_COOKIE.text, setCookie))
        });
    }

    public void writeText(OutputStream out, HttpStatus status, String html) throws IOException {
        write(out, status, ContentType.HTML.v, html.getBytes(), null);
    }

    private void write(OutputStream out, HttpStatus s, String ct, byte[] bytes, HeaderPair[] extra) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeBytes("HTTP/1.1 " + s.code + " " + s.reason + "\r\n");
        dos.writeBytes(HeaderName.CONNECTION.text + ": close\r\n");
        dos.writeBytes(HeaderName.X_CONTENT_TYPE_OPTIONS.text + ": nosniff\r\n");
        if (ct != null) dos.writeBytes(HeaderName.CONTENT_TYPE.text + ": " + ct + "\r\n");
        if (extra != null) for (HeaderPair h: extra) if (h!=null) dos.writeBytes(h.k + ": " + h.v + "\r\n");
        dos.writeBytes(HeaderName.CONTENT_LENGTH.text + ": " + bytes.length + "\r\n");
        dos.writeBytes("\r\n");
        dos.write(bytes);
        dos.flush();
    }

    private static String guess(String p){
        String s = p.toLowerCase();
        if (s.endsWith(".html")||s.endsWith(".htm")) return ContentType.HTML.v;
        if (s.endsWith(".css")) return ContentType.CSS.v;
        if (s.endsWith(".js")) return ContentType.JS.v;
        if (s.endsWith(".png")) return ContentType.PNG.v;
        if (s.endsWith(".jpg")||s.endsWith(".jpeg")) return ContentType.JPG.v;
        if (s.endsWith(".gif")) return ContentType.GIF.v;
        if (s.endsWith(".svg")) return ContentType.SVG.v;
        if (s.endsWith(".json")) return ContentType.JSON.v;
        return ContentType.OCTET.v;
    }

    private static class HeaderPair { final String k; final String v; HeaderPair(String k,String v){this.k=k;this.v=v;} }
}