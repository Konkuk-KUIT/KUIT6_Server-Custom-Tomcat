package controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class LoginController implements Controller {
    @Override
    public void process(HttpRequest request, HttpResponse response) {
        User user = MemoryUserRepository.getInstance().findUserById(request.getParameter("userId"));

        if (user != null && user.getPassword().equals(request.getParameter("password"))) {
            response.sendRedirectWithCookie("/index.html", "logined=true; Path=/");
        } else {
            response.sendRedirect("/user/login_failed.html");
        }
    }
}