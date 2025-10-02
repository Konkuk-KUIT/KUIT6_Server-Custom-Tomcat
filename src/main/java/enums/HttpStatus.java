package enums;

public enum HttpStatus {
    OK("200 OK"),
    FOUND("302 Found"),
    NOT_FOUND("404 Not Found");

    private final String value;

    HttpStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String toStatusLine() {
        return "HTTP/1.1 " + value + "\r\n";
    }
}