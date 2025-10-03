package webserver;

public enum UrlPath {
    ROOT("/"),
    INDEX("/index.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN("/user/login"),
    USER_LOGIN_PAGE("/user/login.html"),
    USER_LOGIN_FAILED("/user/login_failed.html"),
    USER_LIST("/user/list.html"),
    USER_LIST_ALIAS("/user/userList"),
    USER_LIST_HTML("/user/userList.html");

    private final String value;

    UrlPath(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public boolean is(String path) {
        return value.equals(path);
    }
}
