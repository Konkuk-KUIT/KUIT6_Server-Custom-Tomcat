package controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static enumclasses.URL.HOME;
import static enumclasses.UserFactor.*;
import static enumclasses.UserFactor.EMAIL;

public class SignUpController implements Controller {
    private static final Logger log = Logger.getLogger(SignUpController.class.getName());

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        Map<String, String> params = request.getParam();
        User user = new User(params.get(USERID.key), params.get(PASSWORD.key),
                params.get(NAME.key), params.get(EMAIL.key));
        MemoryUserRepository.getInstance().addUser(user);
        log.log(Level.INFO, user.getName());
        response.redirect(HOME.URL);
    }
}
