package controller;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class LoginController implements Controller {

    // /user/login
    MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();


    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {

        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(request.getBody());
        User loginUser = memoryUserRepository.findUserById(queryParameter.get("username"));

        if(loginUser != null) {
            response.redirect("/index.html", "logined=true");
            return;
        }

        response.redirect("/user/login_failed.html", null);
    }
}
