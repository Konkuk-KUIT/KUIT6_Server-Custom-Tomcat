package http.constant;

public enum ApiPath {
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    USER_LIST("/user/userList"),
    INDEX("/index.html"),
    LOGIN_FAILED("/user/login_failed.html");

    private final String path;

    ApiPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public boolean matches(String requestPath) {
        return this.path.equals(requestPath);
    }
}