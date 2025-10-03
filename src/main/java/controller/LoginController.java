package controller;

import db.MemoryUserRepository;
import db.Repository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.Map;
import java.util.logging.Logger;

import static enumclasses.RedirectTarget.LOGIN_FAILED;
import static enumclasses.UserFactor.PASSWORD;
import static enumclasses.UserFactor.USERID;

public class LoginController implements Controller {
    private static final Logger log = Logger.getLogger(LoginController.class.getName());


    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Map<String, String> params = request.getParam();
        User loginUser = MemoryUserRepository.getInstance().findUserById(params.get(USERID.key));

        if (loginUser == null) {
            response.forward(LOGIN_FAILED.route);
        }else if (loginUser.getPassword().equals(params.get(PASSWORD.key))) {
            response.redirect(request.getPath());
        } else {
            response.forward(LOGIN_FAILED.route);
        }
    }
}
