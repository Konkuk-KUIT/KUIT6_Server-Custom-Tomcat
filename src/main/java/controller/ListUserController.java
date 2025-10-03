package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

public class ListUserController implements Controller {
    @Override
    public void process(HttpRequest request, HttpResponse response) {
        if (!request.isLoggedIn()) {
            response.sendRedirect("/user/login.html");
            return;
        }
        response.forward("/user/list.html");
    }
}