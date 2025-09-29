package http.enums;

public enum HttpStatus {
    OK(200, "HTTP/1.1 200 OK"),
    FOUND(302, "HTTP/1.1 302 Found"),
    NOT_FOUND(404, "HTTP/1.1 404 Not Found");

    private final int code;
    private final String statusLine;

    HttpStatus(int code, String statusLine) {
        this.code = code;
        this.statusLine = statusLine;
    }

    public int getCode() {
        return code;
    }

    public String getStatusLine() {
        return statusLine;
    }
}