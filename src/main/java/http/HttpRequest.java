package http;

import constant.HttpMethod;
import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String, String> headers;
    private final String body;

    private HttpRequest(HttpMethod method, String path, String version,
                        Map<String, String> headers, String body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        // <요청 라인>
        String requestLine = br.readLine();
        String[] tokens = requestLine.split(" ");
        HttpMethod method = HttpMethod.from(tokens[0]);
        String path = tokens[1];
        String version = tokens[2];

        // <헤더 라인>
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!(line = br.readLine()).equals("")) { // 빈 줄 직전까지 읽기
            String[] headerTokens = line.split(": ");
            headers.put(headerTokens[0], headerTokens[1]);
        }

        // <바디>
        String body = "";
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            body = IOUtils.readData(br, contentLength);
        }

        return new HttpRequest(method, path, version, headers, body);
    }

    // --- Getter ---
    public HttpMethod getMethod() { return method; }
    public String getPath() {
        if (path.contains("?")) {
            return path.split("\\?")[0];
        }
        return path;
    }
    public String getVersion() { return version; }
    public Map<String, String> getHeaders() { return headers; }
    public String getBody() { return body; }
    public String getQueryString() {
        if (path.contains("?")) {
            return path.split("\\?")[1];
        }
        return "";
    }
}
