package http.request;

import http.enums.HttpHeader;
import http.enums.HttpMethod;
import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final String path;
    private final Map<String, String> headers;
    private final Map<String, String> params;

    private HttpRequest(HttpMethod method, String path, Map<String, String> headers, Map<String, String> params) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.params = params;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request line");
        }
        String[] requestLineTokens = requestLine.split(" ");
        HttpMethod method = HttpMethod.valueOf(requestLineTokens[0]);
        String url = requestLineTokens[1];

        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] headerTokens = line.split(": ");
            if (headerTokens.length == 2) {
                headers.put(headerTokens[0], headerTokens[1]);
            }
        }

        Map<String, String> params = new HashMap<>();
        int queryIndex = url.indexOf("?");
        String path;

        if (queryIndex != -1) {
            path = url.substring(0, queryIndex);
            String queryString = url.substring(queryIndex + 1);
            params.putAll(HttpRequestUtils.parseQueryParameter(queryString));
        } else {
            path = url;
        }
        String contentLengthValue = headers.get(HttpHeader.CONTENT_LENGTH.getValue());
        if (contentLengthValue != null) {
            int contentLength = Integer.parseInt(contentLengthValue);
            if (contentLength > 0) {
                String bodyString = IOUtils.readData(br, contentLength);
                params.putAll(HttpRequestUtils.parseQueryParameter(bodyString));
            }
        }

        return new HttpRequest(method, path, headers, params);
    }

    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public Map<String, String> getHeaders() { return headers; }
    public Map<String, String> getParams() { return params; }
}
