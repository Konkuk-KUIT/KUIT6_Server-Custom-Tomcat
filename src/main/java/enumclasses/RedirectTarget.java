package enumclasses;

public enum RedirectTarget {
    LOGIN("/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html"),
    USERLIST("/user/list.html"),
    SIGNUP("/user/form.html");

    public final String route;

    RedirectTarget(String s) {
        this.route = s;
    }
}
