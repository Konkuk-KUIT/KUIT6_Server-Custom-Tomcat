package controller;

import db.MemoryUserRepository;
import http.constant.ApiPath;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class LoginController implements Controller {
    private static final Logger log = Logger.getLogger(LoginController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
        String userId = params.get("userId");
        String password = params.get("password");

        User user = MemoryUserRepository.getInstance().findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            log.info("Login Success: " + userId);
            response.sendRedirectWithCookie(ApiPath.INDEX.getPath(), "logined", "true");
        } else {
            log.warning("Login Failed: " + userId);
            response.redirect(ApiPath.LOGIN_FAILED.getPath());
        }
    }
}