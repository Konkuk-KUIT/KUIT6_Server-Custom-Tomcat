package http.controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.io.IOException;
import java.util.Map;

public class SignUpController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        MemoryUserRepository db = MemoryUserRepository.getInstance();
        Map<String, String> userInfos = HttpRequestUtils
                .parseQueryParameter(request.getBody());
        db.addUser(new User(userInfos.get("userId")
                , userInfos.get("password"), userInfos.get("name"), userInfos.get("email")));
        response.redirectLogin("/index.html");
    }
}
