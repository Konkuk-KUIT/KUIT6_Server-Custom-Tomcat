package webserver;

public enum UrlPath {
    ROOT("/"),
    INDEX("/index.html"),
    USER_SIGNUP("/user/signup"),
    USER_SIGNUP_FORM("/user/form.html"),
    USER_LOGIN("/user/login"),
    USER_LOGIN_PAGE("/user/login.html"),
    USER_LOGIN_FAILED("/user/login_failed.html"),
    USER_LIST("/user/list"),
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
