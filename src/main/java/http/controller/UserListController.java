package main.java.http.controller;

import main.java.http.*;

import java.io.IOException;

public class UserListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if (!"true".equals(request.getCookie("logined"))) {
            response.redirect("/index.html");
            return;
        }
        response.forward("/user/list.html");
    }
}
