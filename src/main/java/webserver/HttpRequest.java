package webserver;

import webserver.enums.HeaderName;
import webserver.enums.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;

public class HttpRequest {
    private final HttpMethod method;
    private final String path;          // "/index.html"
    private final String queryString;   // "a=1&b=2" or null
    private final String version;       // "HTTP/1.1"
    private final Map<String,String> headers;
    private final String body;

    private HttpRequest(HttpMethod m, String path, String qs, String ver,
                        Map<String,String> headers, String body) {
        this.method = m; this.path = path; this.queryString = qs;
        this.version = ver; this.headers = headers; this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) throw new IOException("Empty request line");
        String[] p = requestLine.split(" ");
        if (p.length != 3) throw new IOException("Invalid request line: "+requestLine);

        HttpMethod method = HttpMethod.valueOf(p[0]);
        String rawPath = p[1];
        String version = p[2];

        String path = rawPath;
        String qs = null;
        int q = rawPath.indexOf('?');
        if (q >= 0) { path = rawPath.substring(0, q); qs = rawPath.substring(q+1); }
        if ("/".equals(path)) path = "/index.html";

        Map<String,String> headers = new LinkedHashMap<>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) break;
            int idx = line.indexOf(':');
            if (idx > 0) headers.put(line.substring(0,idx).trim(), line.substring(idx+1).trim());
        }

        int contentLength = 0;
        String cl = headers.get(HeaderName.CONTENT_LENGTH.text);
        if (cl != null) try { contentLength = Integer.parseInt(cl); } catch (NumberFormatException ignore){}

        String body = "";
        if (contentLength > 0) {
            char[] buf = new char[contentLength];
            int read=0;
            while (read < contentLength) {
                int r = br.read(buf, read, contentLength-read);
                if (r == -1) break; read += r;
            }
            body = new String(buf, 0, read);
        }
        return new HttpRequest(method, path, qs, version, headers, body);
    }

    public HttpMethod method(){ return method; }
    public String path(){ return path; }
    public String queryString(){ return queryString; }
    public String version(){ return version; }
    public Map<String,String> headers(){ return headers; }
    public String body(){ return body; }

    public Map<String,String> queryParams(){
        return parseQuery(queryString);
    }
    public Map<String,String> formParams(){
        return parseQuery(body);
    }
    public Map<String,String> cookies(){
        Map<String,String> map = new HashMap<>();
        String cookieHeader = headers.get(HeaderName.COOKIE.text);
        if (cookieHeader == null) return map;
        for (String token : cookieHeader.split(";")){
            String[] kv = token.trim().split("=",2);
            if (kv.length==2) map.put(kv[0].trim(), kv[1].trim());
        }
        return map;
    }

    private Map<String,String> parseQuery(String qs){
        Map<String,String> map = new HashMap<>();
        if (qs == null || qs.isEmpty()) return map;
        for (String pair : qs.split("&")){
            String[] kv = pair.split("=",2);
            String k = decode(kv[0]); String v = kv.length==2? decode(kv[1]): "";
            map.put(k, v);
        }
        return map;
    }
    private String decode(String s){
        try { return URLDecoder.decode(s, "UTF-8"); } catch (Exception e){ return s; }
    }
}