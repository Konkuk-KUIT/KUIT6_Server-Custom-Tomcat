package webserver;

public enum RequestRouter {
    SIGN_UP_REQ("/user/signup"),
    LOG_IN_REQ("/user/login"),
    USER_LIST_REQ("/user/userList");

    private String path;
    RequestRouter(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
