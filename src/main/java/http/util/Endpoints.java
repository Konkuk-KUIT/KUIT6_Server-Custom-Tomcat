package http.util;

public enum Endpoints {
    ROOT("/"),
    INDEX("/index.html"),
    USER_LOGIN("/user/login"),
    USER_SIGNUP("/user/signup"),
    USER_LIST_ALIAS("/user/userList"),
    USER_LIST_FILE("/user/list.html"),
    USER_LOGIN_FILE("/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html");

    private final String path;

    Endpoints(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
