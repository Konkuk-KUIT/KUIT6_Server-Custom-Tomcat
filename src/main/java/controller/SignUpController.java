//GET/POST 회원가입 요청 처리 후 index.html 리다이렉트
package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;
import webserver.UrlPath;
import webserver.UserQueryKey;
import java.util.Map;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest req, HttpResponse resp) throws Exception {
        Map<String, String> paramMap = req.getMethod().equals(webserver.HttpMethod.GET) ? req.getQueryParameters() : req.getBodyParameters();

        String userId = paramMap.getOrDefault(UserQueryKey.USER_ID.getKey(), "").trim();
        String password = paramMap.getOrDefault(UserQueryKey.PASSWORD.getKey(), "").trim();
        String name = paramMap.getOrDefault(UserQueryKey.NAME.getKey(), "").trim();
        String email = paramMap.getOrDefault(UserQueryKey.EMAIL.getKey(), "").trim();

        User newUser = new User(userId, password, name, email);
        MemoryUserRepository.getInstance().addUser(newUser);

        resp.redirect(UrlPath.INDEX.getPath());
    }
}
