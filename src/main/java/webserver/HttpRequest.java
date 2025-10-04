package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private boolean isLogin = false;
    private int contentLength = 0;
    private String body;
    MemoryUserRepository userDB = MemoryUserRepository.getInstance();
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> params = new HashMap<>(); // 쿼리 파라미터 또는 본문 데이터

    // 생성자에서 InputStream을 받아 요청 정보를 모두 파싱합니다.
    public HttpRequest(InputStream in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String requestLine = br.readLine();
        if (requestLine == null) {
            return;
        }
        //  "GET /index.html HTTP/1.1"
        String[] tokens = requestLine.split(" ");
        this.method = tokens[0];
        this.path = tokens[1];
        // 2. 헤더 파싱 (빈 줄이 나올 때까지)
        String line;
        while (true) {
            line = br.readLine();
            if(line == null || line.equals("")) break;
            if (line.startsWith("Content-Length")) {
                this.contentLength = Integer.parseInt(line.split(": ")[1]);
            }
            if(line.startsWith("Cookie")) {
                String cookieHeader = line.substring("Cookie: ".length()).trim();
                Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieHeader);
                String loginCookie = cookies.get("logined");
                if ("true".equals(loginCookie)) {
                    this.isLogin = true;
                }
            }

        }
        if (this.contentLength > 0) {
            char[] bodyData = new char[this.contentLength];
            br.read(bodyData, 0, this.contentLength);
            this.body = new String(bodyData);
        }
    }

    // 파싱된 정보를 쉽게 가져올 수 있는 getter 메소드들
    public String getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }

    public boolean isLogin() {
        return this.isLogin;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public MemoryUserRepository getUserDB() {
        return this.userDB;
    }

    public String getBody() {
        return this.body;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}