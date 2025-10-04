package webserver;

public enum UrlPath {
    INDEX("/index.html"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    LOGIN_FAILED("/user/login_failed"),
    LIST("/user/list");

    private final String path;

    UrlPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
