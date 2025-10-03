package controller;

import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpResponse;
import model.User;
import model.UserFormKey;
import http.util.Endpoints;

import java.util.logging.Level;
import java.util.logging.Logger;

public class UserLoginController implements Controller {
    private static final Logger log = Logger.getLogger(UserLoginController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        User user = MemoryUserRepository.getInstance().findUserById(request.getParameter(UserFormKey.USER_ID.getKey()));

        if (user != null && user.getPassword().equals(request.getParameter(UserFormKey.PASSWORD.getKey()))) {
            log.log(Level.INFO, "Login successful for user: " + user.getUserId());
            response.sendLoginSuccessRedirect(Endpoints.INDEX.getPath());
        } else {
            log.log(Level.INFO, "Login failed for user ID: " + request.getParameter(UserFormKey.USER_ID.getKey()));
            response.redirect(Endpoints.LOGIN_FAILED.getPath());
        }
    }
}