package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;
import http.util.Endpoints;

public class UserListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        if (request.isLoggedIn()) {
            response.forward(Endpoints.USER_LIST_FILE.getPath());
        } else {
            response.redirect(Endpoints.USER_LOGIN_FILE.getPath());
        }
    }
}