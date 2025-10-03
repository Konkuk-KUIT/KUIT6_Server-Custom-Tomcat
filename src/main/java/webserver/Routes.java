package webserver;

public enum Routes {
    SHOW_URL("/qna/show.html", "webapp/qna/form.html"),
    FORM_URL("/user/form.html","webapp/user/form.html"),
    USER_LIST_URL("/user/userList","webapp/user/list.html"),
    LOGIN_URL("/user/login.html","webapp/user/login.html"),
    LOGIN_FAIL_URL("/user/login_failed.html","webapp/user/login_failed.html"),
    STYLE_URL("/css/styles.css", "webapp/css/styles.css"),
    BASE_URL("/", "webapp/index.html");

    private final String path;
    private final String filePath;

    Routes(String path, String filePath) {
        this.path = path;
        this.filePath = filePath;
    }

    public static String getFilePath(String path) {
        for(Routes route : values()) {
            if(route.path.equals(path)) {
                return route.filePath;
            }
        }
        return BASE_URL.filePath;
    }
}