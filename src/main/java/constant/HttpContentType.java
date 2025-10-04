package constant;

public enum HttpContentType {
    HTML("text/html;charset=utf-8"),
    CSS("text/css;charset=utf-8"),
    JS("application/javascript;charset=utf-8"),
    JSON("application/json;charset=utf-8"),
    TEXT("text/plain;charset=utf-8");

    private final String value;

    HttpContentType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}