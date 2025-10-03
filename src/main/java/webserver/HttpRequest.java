package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class HttpRequest {
    HttpMethod method;
    String url;
    String reqBody;
    HashMap<String, String> cookies;
    int contentLength;

    private HttpRequest(HttpMethod method, String url, HashMap<String, String> cookies, int contentLength, String reqBody) {
        this.method = method;
        this.url = url;
        this.contentLength = contentLength;
        this.cookies = cookies;
        this.reqBody = reqBody;
    }

    public static HttpRequest from(BufferedReader br) throws IOException {
        String reqHeaderFirstLine = br.readLine();
        String[] tokens = reqHeaderFirstLine.split(" ");
        HashMap<String, String> headerContent = readRequestHeader(br);
        int contentLength = getIntegerContentLength(headerContent);
        HashMap<String, String> cookies = extractCookieFromHeader(headerContent);
        String reqBody = readRequestBody(br, contentLength);
        return new HttpRequest(HttpMethod.valueOf(tokens[0]), tokens[1], cookies, contentLength, reqBody);
    }

    private static int getIntegerContentLength(HashMap<String, String> headerContent) {
        return Integer.parseInt(headerContent.getOrDefault("Content-Length", "0"));
    }
    private static HashMap<String, String> extractCookieFromHeader(HashMap<String, String> headerContent) {
        HashMap<String, String> cookies = new HashMap<>(headerContent);
        cookies.remove("Content-Length");
        return cookies;
    }

    public boolean isPost() {
        return method == HttpMethod.POST;
    }
    public boolean isGet() {return method == HttpMethod.GET;}

    public boolean isSameUrl(String url) {
        return this.url.equals(url);
    }

    public boolean isCss() {
        return url.endsWith(".css");
    }

    public String getFilePath() {
        return Routes.getFilePath(url);
    }

    private static HashMap<String, String> readRequestHeader(BufferedReader br) throws IOException {
        String line;
        HashMap<String, String> headerContents = new HashMap<>();
        while(!(line = br.readLine()).isEmpty()) {
            if(isStartedWithContentLength(line)){
                int contentLength = getContentLength(line);
                headerContents.put("Content-Length", String.valueOf(contentLength));
            } else if(isStartedWithCookie(line)) {
                HashMap<String, String> cookieMap = getCookie(line);
                headerContents.putAll(cookieMap);
            }
        }
        return headerContents;
    }
    private static String readRequestBody(BufferedReader br, int contentLength) throws IOException {
        char[] bodyData = new char[contentLength];
        br.read(bodyData, 0, contentLength);
        return new String(bodyData);
    }

    private static int getContentLength(String line) throws IOException {
        return Integer.parseInt(line.split(":")[1].trim());
    }

    private static boolean isStartedWithContentLength(String line) {
        return line.startsWith("Content-Length:");
    }
    private static boolean isStartedWithCookie(String line) {
        return line.startsWith("Cookie:");
    }

    private static HashMap<String, String> getCookie(String line) throws IOException {
        String headerCookies = line.split(":")[1].trim();
        String[] cookies = headerCookies.split(";");
        HashMap<String, String> cookiesMap = new HashMap<>();
        for (String cookie : cookies) {
            cookiesMap.put(cookie.split("=")[0].trim(), cookie.split("=")[1].trim());
        }
        return cookiesMap;
    }

    public HashMap<String, String> getCookies() {
        return cookies;
    }

    public String getReqBody() {
        return reqBody;
    }

    public byte[] getByteBody() throws IOException {
        String filePath = getFilePath();
        return Files.readAllBytes(Paths.get(filePath));
    }

    public String getUrl() {
        return url;
    }
}
