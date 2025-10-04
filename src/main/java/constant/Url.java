package constant;

public enum Url {
    ROOT("/", "./webapp/index.html"),
    CSS("", "./webapp"),
    INDEX("/index.html", "./webapp/index.html"),
    USER_FORM("/user/form.html", "./webapp/user/form.html"),
    USER_SIGNUP("/user/signup", null),
    USER_LOGIN("/user/login", null),
    USER_LOGIN_HTML("/user/login.html", "./webapp/user/login.html"),
    USER_LOGIN_FAILED("/user/login_failed.html", "./webapp/user/login_failed.html"),
    USER_LIST("/user/userList", "./webapp/user/list.html");

    private final String path;
    private final String filePath;

    Url(String path, String filePath) {
        this.path = path;
        this.filePath = filePath;
    }

    public String path() {
        return path;
    }

    public String filePath() {
        return filePath;
    }
}