package webserver;

public enum Path {
    INDEX_FILE("webapp/index.html"),
    QNA_SHOW_FILE("webapp/qna/show.html"),
    USER_FORM_FILE("webapp/user/form.html"),
    USER_LOGIN_FILE("webapp/user/login.html"),
    USER_LOGIN_FAIL_FILE("webapp/user/login_failed.html"),
    USER_LIST_FILE("webapp/user/list.html"),
    CSS_FILE("webapp/css/styles.css");

    private final String path;

    public String getPath() {
        return path;
    }

    Path(String path) {
        this.path = path;
    }
}
