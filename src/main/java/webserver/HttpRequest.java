package webserver;

import http.util.HttpRequestUtils;
import http.util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enumclasses.HttpHeader.*;
import static enumclasses.HttpMethod.POST;
import static enumclasses.URL.HOME;


public class HttpRequest {

    private static final Logger log = Logger.getLogger(HttpRequest.class.getName());
    private String path;
    private String method;
    private int contentLength;
    private boolean logined = false;
    private Map<String, String> header = new HashMap<String,String>();
    private Map<String, String> param = new HashMap<String,String>();

    private HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String request = br.readLine();
            if (request == null || request.isEmpty()) {
                return;
            }
            //request :HTTPMethod Request-URL HTTPVersion
            //method extract
            extractPathAndMethod(request);

            if (path.equals("/")) {
                path = HOME.URL;
            }

            // header extract
            request = br.readLine();
            while(!request.equals(EMPTY.text)){
                if (request.contains(CONTENT_LENGTH.text)) {
                    contentLength = extractContentLength(request);
                }
                if (request.contains(COOKIE.text)) {
                    logined = isLogined(request);
                }
                request = br.readLine();
            }
            if (POST.name().equals(method)) {
                String body = IOUtils.readData(br, contentLength);
                param = HttpRequestUtils.parseQueryParameter(body);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void extractPathAndMethod(String request) {
        String[] part = request.split(" ");
        method = part[0];
        if (POST.name().equals(method)) {
            path = part[1];
            return;
        }

        int index = part[1].indexOf("?");
        if (index == -1) {
            path = part[1];
        } else {
            path = part[1].substring(0,index);
            param = HttpRequestUtils.parseQueryParameter(part[1].substring(index + 1));
        }
    }

    public static HttpRequest from(InputStream in) {
        return new HttpRequest(in);
    }

    private int extractContentLength(String request) {
        String[] headerTokens = request.split(": ");
        return Integer.parseInt(headerTokens[1].trim());
    }


    private boolean isLogined(String request) {
        String[] headerTokens = request.split(": ");
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public boolean isLogined() {
        return logined;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public Map<String, String> getParam() {
        return param;
    }
}
