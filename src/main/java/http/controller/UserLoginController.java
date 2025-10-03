package main.java.http.controller;


import main.java.http.*;
import main.java.http.enums.QueryKey;
import main.java.model.User;
import main.java.db.MemoryUserRepository;

import java.io.IOException;


public class UserLoginController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String userId = request.getParam(QueryKey.USER_ID.getKey());
        String password = request.getParam(QueryKey.PASSWORD.getKey());
        User found = MemoryUserRepository.getInstance().findUserById(userId);

        if (found != null && found.getPassword().equals(password)) {
            response.redirectWithCookie("/index.html", "logined=true; Path=/");
        } else {
            response.redirect("/user/login_failed.html");
        }
    }
}
