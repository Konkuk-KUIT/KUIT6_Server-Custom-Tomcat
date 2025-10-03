package webserver;

public enum Url {
    ROOT("/"),
    INDEX("/index.html"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    LOGIN_FAILED("/user/login_failed.html"),
    USER_LIST("/user/list");

    private final String path;

    Url(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
