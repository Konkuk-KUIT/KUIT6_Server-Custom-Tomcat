package enums;

public enum HtmlUrls {
    INDEX("/index.html"),
    LOGIN("/user/login.html"),
    LOGIN_FAIL("/user/login_failed.html"),
    USERLIST("/user/list.html");

    private final String path;

    HtmlUrls(String path) { this.path = path; }

    public String getPath() { return path; }
}