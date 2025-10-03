package webserver;

public enum URL {
    DEFAULT("/"),
    INDEX("/index.html"),
    QNA_SHOW("/qna/show.html"),
    USER_FROM("/user/form.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN_HTML("/user/login.html"),
    USER_LOGIN("/user/login"),
    USER_USERLIST("user/userList");

    private final String url;

    public String getUrl() {
        return url;
    }

    URL(String url) {
        this.url = url;
    }
}
