//로그인 처리, 성공 시 쿠키 설정 후 index.html 리다이렉트, 실패 시 login_failed.html 리다이렉트
package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;
import webserver.UrlPath;
import webserver.UserQueryKey;
import webserver.HttpMethod;

import java.util.Map;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest req, HttpResponse resp) throws Exception {
        if (req.getMethod() != HttpMethod.POST) {
            // GET 로그인 폼 요청은 Forward
            resp.forward(UrlPath.LOGIN.getPath());
            return;
        }

        Map<String, String> paramMap = req.getBodyParameters();
        String userId = paramMap.getOrDefault(UserQueryKey.USER_ID.getKey(), "").trim();
        String password = paramMap.getOrDefault(UserQueryKey.PASSWORD.getKey(), "").trim();

        User user = MemoryUserRepository.getInstance().findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            // 로그인 성공
            resp.redirectWithCookie(UrlPath.INDEX.getPath(), "logined=true; Path=/");
        } else {
            // 로그인 실패
            resp.redirect(UrlPath.LOGIN_FAILED.getPath());
        }
    }
}

