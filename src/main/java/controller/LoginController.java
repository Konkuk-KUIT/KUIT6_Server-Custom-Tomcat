package controller;

import db.MemoryUserRepository;
import enums.RequestPath;
import enums.UserParam;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = request.getBody().getFormData();

        String userId = params.get(UserParam.USER_ID.getKey());
        String password = params.get(UserParam.PASSWORD.getKey());

        MemoryUserRepository repository = MemoryUserRepository.getInstance();
        User user = repository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) { // 로그인 성공
            response.sendRedirectWithCookie(RequestPath.INDEX.getPath(), "logined=true; Path=/");
        } else { // 로그인 실패
            response.sendRedirect(RequestPath.USER_LOGIN_FAILED.getPath());
        }
    }
}
