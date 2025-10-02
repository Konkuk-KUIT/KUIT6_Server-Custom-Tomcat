package enums;

public enum RequestPath {
    ROOT("/", "/index.html"),
    INDEX("/index.html"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    USER_LIST("/user/userList"),
    USER_LOGIN_FAILED("/user/login_failed.html");


    private final String path;
    private final String redirect; // ROOT 같은 경우 "/" 요청을 "/index.html"로 바꿔야 해서

    RequestPath(String path) {
        this(path, path);
    }

    RequestPath(String path, String redirect) {
        this.path = path;
        this.redirect = redirect;
    }

    public String getPath() {
        return path;
    }

    public String getRedirect() {
        return redirect;
    }

    public boolean matches(String requestPath) {
        return this.path.equalsIgnoreCase(requestPath);
    }
}