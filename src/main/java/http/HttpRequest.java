package http;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>();

    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String line = br.readLine();
        if (line == null) {
            return;
        }

        String[] tokens = line.split(" ");
        this.method = tokens[0];
        this.path = tokens[1];

        String headerLine;
        while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerTokens = headerLine.split(":", 2);
            if (headerTokens.length == 2) {
                headers.put(headerTokens[0].trim(), headerTokens[1].trim());
            }
        }

        if ("POST".equals(method)) {
            String contentLengthHeader = getHeader("Content-Length");
            if (contentLengthHeader != null) {
                int contentLength = Integer.parseInt(contentLengthHeader);
                String body = IOUtils.readData(br, contentLength);
                this.params = HttpRequestUtils.parseQueryParameter(body);
            }
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public String getParameter(String name) {
        return params.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
