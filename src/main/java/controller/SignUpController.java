package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;
import model.UserQueryKey;

import java.io.IOException;
import java.util.Map;

public class SignUpController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = request.getParams();
        User user = new User(
                params.get(UserQueryKey.USER_ID.getKey()),
                params.get(UserQueryKey.PASSWORD.getKey()),
                params.get(UserQueryKey.NAME.getKey()),
                params.get(UserQueryKey.EMAIL.getKey())
        );

        MemoryUserRepository.getInstance().addUser(user);
        response.redirect("/index.html");
    }
}
