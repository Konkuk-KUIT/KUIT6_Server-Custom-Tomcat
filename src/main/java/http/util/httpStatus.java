package http.util;

public enum httpStatus {
    OK("HTTP/1.1 200 OK \r\n"),
    FOUND("HTTP/1.1 302 Found \r\n"),
    NOT_FOUND("HTTP/1.1 404 Not Found \r\n");

    private final String statusLine;

    httpStatus(String statusLine) {
        this.statusLine = statusLine;
    }

    public String getStatusLine() {
        return statusLine;
    }
}
