package main.java.http.enums;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String reason;

    HttpStatus(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public String format() {
        return "HTTP/1.1 " + code + " " + reason + "\r\n";
    }
}
