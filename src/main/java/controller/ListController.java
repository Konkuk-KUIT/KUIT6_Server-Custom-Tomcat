package controller;

import http.HttpRequest;
import http.HttpResponse;
import webserver.UrlPath;

import java.io.IOException;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String logined = request.getCookie("logined");
        if (!"true".equalsIgnoreCase(logined)) {
            response.response302Header(UrlPath.USER_LOGIN_PAGE.value());
            return;
        }

        String path = request.getPath();
        if (path == null) path = "/user/list.html";
        int q = path.indexOf('?');
        if (q >= 0) path = path.substring(0, q);
        String normalized = path.startsWith("/") ? path.substring(1) : path;

        // 3) /user/list 와 /user/list.html 모두 같은 파일로 매핑
        if (normalized.equals("user/list") || normalized.equals("user/list.html")) {
            response.forward("user/list.html");   // ★ 항상 .html로
            return;
        }

        // 4) 그 외는 필요에 따라 404 또는 다른 처리
        response.send404(normalized);

//        String resource = "user/list.html";
//        if (UrlPath.USER_LIST.value().equals(path) || UrlPath.USER_LIST_HTML.value().equals(path)) {
//            resource = path.startsWith("/") ? path.substring(1) : path;
//        }
//        response.forward(resource);
    }
}
