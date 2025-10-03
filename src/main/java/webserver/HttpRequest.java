package webserver;

import enums.HttpHeader;
import http.util.IOUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

     String method;
     String path;
     String version;
     Map<String,String> headers;
     String body;

     private HttpRequest(String[] httpStartLine, Map<String,String> headers, String body) {

         this.method = httpStartLine[0];
         this.path = httpStartLine[1];
         this.version = httpStartLine[2];
         this.headers = headers;
         this.body = body;
     }

    public String getMethod() {
         return method;
    }

    public String getUrl() {
         return path;
    }

    public Map<String,String> getHeaders() {
         return headers;
    }

    public String getBody() {
         return body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {

        //접속 루트 읽기
        String line = br.readLine();
        if(line == null || line.isEmpty()) return null;
        String[] tokens = line.split(" ");

        //헤더 읽기
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
            int idx = headerLine.indexOf(":");
            if (idx > -1) {
                String headerName = headerLine.substring(0, idx).trim();
                String headerValue = headerLine.substring(idx + 1).trim();
                headers.put(headerName, headerValue);
            }
        }

        int contentLength = 0;

        //content-Length 확인
        if(headers.containsKey(HttpHeader.CONTENT_LENGTH.value())) {
            contentLength = Integer.parseInt(headers.get(HttpHeader.CONTENT_LENGTH.value()));
        }

        //본문 읽기
        String body = IOUtils.readData(br, contentLength);

        return new HttpRequest(tokens, headers, body);
    }



}
