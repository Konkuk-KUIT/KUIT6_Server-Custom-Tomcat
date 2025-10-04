package controller;

import http.CookieName;
import http.HttpRequest;
import http.HttpResponse;
import webserver.UrlPath;

import java.io.IOException;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String logined = request.getCookie(CookieName.LOGINED.key());
        if (!"true".equalsIgnoreCase(logined)) {
            response.forward(UrlPath.USER_LOGIN_PAGE.value());
            return;
        }

        response.forward(UrlPath.USER_LIST_HTML.value());

//        String path = request.getPath();
//        if (path == null) path = "/user/list";
//        int q = path.indexOf('?');
//        if (q >= 0) path = path.substring(0, q);
//        String normalized = path.startsWith("/") ? path.substring(1) : path;
//
//        // 3) 표준 경로만 서비스 (user/list, user/list.html, user/list/ 허용)
//        if (normalized.equals("user/list") || normalized.equals("user/list.html") || normalized.equals("user/list/")) {
//            response.forward("user/list.html");           // 실제 파일로 고정
//            return;
//        }
//
//        // 4) 과거/실수 경로 보정: /user/userList* → /user/list 로 리다이렉트
//        if (normalized.equals("user/userList") || normalized.equals("user/userList.html") || normalized.equals("user/userList/")) {
//            response.response302Header("/user/list.html");
//            return;
//        }

//        if (normalized.startsWith("user/userList")) {
//            response.forward("user/list.html");
//            return;
//        }

        // 4) 그 외는 필요에 따라 404 또는 다른 처리
        response.send404("/user/userList");

    }
}
