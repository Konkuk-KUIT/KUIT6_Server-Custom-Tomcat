package http.util;

public enum UrlPath {
    HOME("/", "webapp/index.html"),
    QNA_SHOW("/qna/show.html", "webapp/qna/show.html"),
    QNA_FORM("/qna/form.html", "webapp/qna/form.html"),
    USER_FORM("/user/form.html", "webapp/user/form.html"),
    USER_LIST("/user/list.html", "webapp/user/list.html"),
    LOGIN_PAGE("/user/login.html", "webapp/user/login.html"),
    LOGIN_FAILED("/user/login_failed.html", "webapp/user/login_failed.html"),
    ROOT("webapp", null);
    private final String path;
    private final String filePath;

    UrlPath(String path, String filePath) {
        this.path = path;
        this.filePath = filePath;
    }

    public static UrlPath from(String requestURI) {
        if (requestURI == null) return null;
        int q = requestURI.indexOf('?');
        String onlyPath = (q >= 0) ? requestURI.substring(0, q) : requestURI;

        for (UrlPath p : values()) {
            if (p.path.equals(onlyPath)) {
                return p;
            }
        }
        return null;
    }

    public String getPath() {
        return path;
    }

    public String getFilePath() {
        return filePath;
    }

}
