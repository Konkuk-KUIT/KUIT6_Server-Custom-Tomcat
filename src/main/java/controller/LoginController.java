package controller;

import db.MemoryUserRepository;
import http.enums.HttpMethod;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;
import model.UserQueryKey;

import java.io.IOException;
import java.util.Map;

public class LoginController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getMethod() == HttpMethod.POST) {
            Map<String, String> params = request.getParams();
            String userId = params.get(UserQueryKey.USER_ID.getKey());
            String password = params.get(UserQueryKey.PASSWORD.getKey());

            User user = MemoryUserRepository.getInstance().findUserById(userId);
            if (user != null && user.getPassword().equals(password)) {
                response.redirectSuccessLogin("/index.html");
            } else {
                response.redirect("/user/login_failed.html");
            }
        } else {
            response.forward("/user/login.html");
        }
    }
}
