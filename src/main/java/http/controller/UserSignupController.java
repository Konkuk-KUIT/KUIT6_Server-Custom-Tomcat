package main.java.http.controller;


import main.java.http.*;
import main.java.http.enums.QueryKey;
import main.java.model.User;
import main.java.db.MemoryUserRepository;

import java.io.IOException;

public class UserSignupController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        User u = new User(
                request.getParam(QueryKey.USER_ID.getKey()),
                request.getParam(QueryKey.PASSWORD.getKey()),
                request.getParam(QueryKey.NAME.getKey()),
                request.getParam(QueryKey.EMAIL.getKey())
        );
        MemoryUserRepository.getInstance().addUser(u);
        response.redirect("/index.html");
    }
}
