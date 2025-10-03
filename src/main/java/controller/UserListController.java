package controller;

import http.enums.HttpHeader;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public class UserListController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String cookie = request.getHeaders().get(HttpHeader.COOKIE.getValue());
        if (cookie != null && cookie.contains("logined=true")) {
            response.forward("/user/list.html");
        } else {
            response.redirect("/user/login.html");
        }
    }
}
