package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import model.UserQueryKey;
import webserver.UrlPath;
import http.HttpHeader;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Controller {
    private static final Logger log = Logger.getLogger(LoginController.class.getName());
    private final Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getMethod() == HttpMethod.GET) {
            response.forward("user/login.html");
            return;
        }

        if (request.getMethod() != HttpMethod.POST) {
            response.response302Header(UrlPath.USER_LOGIN_FAILED.value());
            return;
        }

        String userId = request.getParameter(UserQueryKey.USER_ID.key());
        User user = repository.findUserById(userId);
        log.log(Level.INFO, "Login attempt userId={0}", userId);
        if (user != null && user.getPassword().equals(request.getParameter(UserQueryKey.PASSWORD.key()))) {
            response.addHeader(HttpHeader.SET_COOKIE, "logined=true");
            log.log(Level.INFO, "Login success userId={0}", userId);
            response.response302Header(UrlPath.INDEX.value());
        } else {
            log.log(Level.INFO, "Login failed userId={0}", userId);
            response.response302Header(UrlPath.USER_LOGIN_FAILED.value());
        }
    }
}
