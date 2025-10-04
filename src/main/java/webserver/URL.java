package webserver;

import java.util.Optional;

public enum URL {
    DEFAULT("/"),
    INDEX("/index.html"),
    QNA_SHOW("/qna/show.html"),
    USER_FROM("/user/form.html"),
    USER_SIGNUP("/user/signup"),
    USER_LOGIN_HTML("/user/login.html"),
    USER_LOGIN("/user/login"),
    USER_USERLIST("/user/userList"),
    USER_USERLIST_HTML("/user/list.html"),
    USER_LOGIN_FAIL("/user/login_failed.html");

    private final String url;

    public String getUrl() {
        return url;
    }

    URL(String url) {
        this.url = url;
    }

    public static Optional<URL> fromPath(String requestTargetRaw) {
        if (requestTargetRaw == null || requestTargetRaw.isEmpty()) return Optional.empty();

        // 1) 쿼리 제거
        int q = requestTargetRaw.indexOf('?');
        String path = (q >= 0) ? requestTargetRaw.substring(0, q) : requestTargetRaw;

        // 2) 비어 있으면 루트
        if (path.isEmpty()) path = "/";

        // 3) 경로만 비교
        for (URL r : values()) {
            if (r.url.equals(path)) return Optional.of(r);
        }
        return Optional.empty();
    }
}
