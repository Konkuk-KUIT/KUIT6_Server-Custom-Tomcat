package controller;

import http.HttpRequest;
import http.HttpResponse;
import http.enums.RequestPath;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserListController implements Controller {
    private static final Logger log = Logger.getLogger(UserListController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String cookieValue = request.getCookie();
        
        // Cookie에서 로그인 상태 확인
        if (cookieValue != null && Objects.equals(parseCookieValue(cookieValue, "logined"), "true")) {
            // 로그인된 사용자: user/list.html 파일 forward
            log.log(Level.INFO, "Logged in user accessing user list");
            response.forward(RequestPath.USER_LIST_HTML.getValue());
        } else {
            // 비로그인 상태: 로그인 페이지로 리다이렉트
            log.log(Level.INFO, "Non-logged user redirected to login page");
            response.redirect(RequestPath.USER_LOGIN_HTML.getValue());
        }
    }

    private String parseCookieValue(String cookie, String key) {
        for (String pair : cookie.split(";\\s*")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2 && kv[0].equals(key)) {
                return kv[1];
            }
        }
        return null;
    }
}