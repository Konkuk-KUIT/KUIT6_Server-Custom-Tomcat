package http.util;

import http.util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpRequest {
    private static final Logger log = Logger.getLogger(HttpRequest.class.getName());

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> params;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, Map<String, String> params) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.params = params;
    }

    /**
     * 정적 팩토리 메서드
     * @param br InputStream으로부터 생성된 BufferedReader
     * @return HttpRequest 객체
     */
    public static HttpRequest from(BufferedReader br) throws IOException {
        // 1. Request Line 파싱
        String line = br.readLine();
        if (line == null) {
            throw new IOException("Empty request");
        }
        RequestLine requestLine = new RequestLine(line);

        // 2. Headers 파싱
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
            String[] headerTokens = headerLine.split(":", 2);
            headers.put(headerTokens[0], headerTokens[1].trim());
        }

        // 3. Body 파싱
        int contentLength = Integer.parseInt(headers.getOrDefault(httpHeader.CONTENT_LENGTH.getValue(), "0"));
        String body = IOUtils.readData(br, contentLength);
        Map<String, String> params = parseQueryString(body);

        return new HttpRequest(requestLine, headers, params);
    }

    // 파싱 로직 (기존 RequestHandler에 있던 것을 가져옴)
    private static Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        try {
            String decoded = URLDecoder.decode(queryString, "UTF-8");
            String[] pairs = decoded.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    params.put(pair.substring(0, idx), pair.substring(idx + 1));
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return params;
    }

    // Getter 메서드들
    public httpMethod getMethod() {
        return this.requestLine.method();
    }

    public String getPath() {
        return this.requestLine.path();
    }

    public String getHeader(String key) {
        return this.headers.get(key);
    }

    public String getParameter(String key) {
        return this.params.get(key);
    }

    public boolean isLoggedIn() {
        String cookieHeader = getHeader(httpHeader.COOKIE.getValue());
        return cookieHeader != null && cookieHeader.contains("logined=true");
    }
}
