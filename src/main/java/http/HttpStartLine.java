package http;

import webserver.URL;

public class HttpStartLine {
    private final HttpMethod method;
    private final URL target;
    private final String version;

    public HttpStartLine(HttpMethod method, URL target, String version) {
        this.method = method;
        this.target = target;
        this.version = version;
    }

    public static HttpStartLine from(String startLine){
        String[] tokens = startLine.split(" ");
        return new HttpStartLine(HttpMethod.valueOf(tokens[0]), URL.valueOf(tokens[1]), tokens[2]);
    }

    public String getMethod() {
        return method.getMethod();
    }

    public String getTarget(){
        return target.getUrl();
    }
}
