package enums;

public enum HttpUrls {
    ROOT("/Users/sooa/Documents/kuit/KUIT6_Server-Custom-Tomcat/webapp"),
    SIGNUP("/user/signup"),
    LOGIN("/user/login"),
    USERLIST("/user/userList");

    private final String path;

    HttpUrls(String path) { this.path = path; }

    public String getPath() { return path; }
}
