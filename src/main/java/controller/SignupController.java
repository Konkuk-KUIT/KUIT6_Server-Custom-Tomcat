package controller;

import db.MemoryUserRepository;
import http.constant.ApiPath;
import http.constant.HttpMethod;
import http.request.HttpRequest;
import http.response.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

public class SignupController implements Controller {
    private static final Logger log = Logger.getLogger(SignupController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params;

        if (request.getMethod() == HttpMethod.POST) {
            params = HttpRequestUtils.parseQueryParameter(request.getBody());
        } else {
            params = HttpRequestUtils.parseQueryParameter(request.getQueryString());
        }

        User user = new User(
                params.get("userId"),
                params.get("password"),
                params.get("name"),
                params.get("email")
        );

        MemoryUserRepository.getInstance().addUser(user);
        log.info("New User Added: " + user.getUserId());

        response.redirect(ApiPath.INDEX.getPath());
    }
}