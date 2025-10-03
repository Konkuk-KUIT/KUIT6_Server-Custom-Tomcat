package enumclasses;

public enum URL {
    HOME("/index.html"),
    LOGIN("/user/login"),
    LOGIN_FAILED("/user/login_failed"),
    SIGNUP("/user/signup"),
    USERLIST("/user/userList");

    public final String URL;
    URL(String u) {
        this.URL = u;
    }
}
