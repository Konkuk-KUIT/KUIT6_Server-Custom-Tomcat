package http.enums;

public enum RequestPath {
    ROOT("/"),
    INDEX("/index.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN("/user/login"),
    USER_LIST("/user/userList"),
    USER_LIST_HTML("/user/list.html"),
    USER_LOGIN_HTML("/user/login.html"),
    USER_LOGIN_FAILED("/user/login_failed.html");

    private final String path;

    RequestPath(String path) {
        this.path = path;
    }

    public String getValue() {
        return path;
    }

    public static RequestPath from(String path) {
        for (RequestPath requestPath : values()) {
            if (requestPath.path.equals(path)) {
                return requestPath;
            }
        }
        throw new IllegalArgumentException("Unknown request path: " + path);
    }
}