package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.logging.Logger;

public class UserListController implements Controller {
    private static final Logger log = Logger.getLogger(UserListController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String loginedValue = request.getCookie("logined");

        if ("true".equals(loginedValue)) {
            log.info("Authorized - Showing user list");
            response.forward("/user/list.html");
        } else {
            log.warning("Unauthorized");
            response.redirect("/user/login.html");
        }
    }
}