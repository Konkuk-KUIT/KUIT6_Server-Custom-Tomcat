package http.constants;

public enum HttpStatusCode {
    SUCCESS("200 OK"),
    REDIRECT("302 Found"),
    CLIENT_ERROR("404 Not Found"),
    SERVER_ERROR("500 Internal Server Error");

    private final String text;
    HttpStatusCode(String text) { this.text = text; }
    @Override public String toString() { return text; }
}
