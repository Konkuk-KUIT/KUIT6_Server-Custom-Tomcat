package webserver;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found"),
    NO_CONTENT(204, "No Content"),
    NOT_FOUND(404, "Not Found");

    private final int code;
    private final String reason;

    HttpStatusCode(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    public int getCode() { return code; }
    public String getReason() { return reason; }
}