package controller;

import constant.Url;
import http.HttpRequest;
import http.HttpResponse;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        String cookie = request.getHeaders().get("Cookie");

        if (cookie != null && cookie.contains("logined=true")) {
            response.response200(Url.USER_LIST.getPath() + ".html"); // ✅ list.html 반환
        } else {
            response.response302Header(Url.USER_LOGIN_HTML.getPath()); // ✅ 로그인 안되면 redirect
        }
    }
}
