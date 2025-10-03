package http.controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        MemoryUserRepository db = MemoryUserRepository.getInstance();
        Map<String, String> userInfos = HttpRequestUtils
                .parseQueryParameter(request.getBody());
        User user = db.findUserById(userInfos.get("userId"));
        if (user == null) {
            response.redirectLoginFail("/user/login_failed.html");
            return;
        }
        if (user.getPassword().equals(userInfos.get("password"))) {
            response.redirectLogin("/index.html");
        } else response.redirectLoginFail("/user/login_failed.html");
    }
}
