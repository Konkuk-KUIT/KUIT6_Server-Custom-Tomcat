package constant;

public enum Url {
    ROOT("/"),
    INDEX("/index.html"),
    USER_CREATE("/user/signup"),
    USER_LOGIN("/user/login"),
    USER_LOGIN_HTML("/user/login.html"),
    USER_LOGIN_FAILED("/user/login_failed.html"),
    USER_LIST("/user/list");

    private final String path;

    Url(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}

