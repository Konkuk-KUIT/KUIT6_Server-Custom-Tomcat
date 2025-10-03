package constant;

public enum HttpStatusCode {
    OK("200 OK"),
    FOUND("302 Found"),
    NOT_FOUND("404 Not Found");

    private final String message;

    HttpStatusCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

