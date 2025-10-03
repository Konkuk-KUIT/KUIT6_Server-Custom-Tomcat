package enumclasses;

public enum StatusCode {
    OK(200, "OK"),
    FOUND(302, "Redirect"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_ERROR(500, "Internal Server Error");
    final int code;
    final String reason;

    StatusCode(int c, String r) {
        this.code = c;
        this.reason = r;
    }

    public String line() {
        return "HTTP/1.1 " + code + " " + reason + " \r\n";
    }
}
