package webserver.controller;

import constant.Url;
import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.DataOutputStream;
import java.util.Map;

public class UserSignupController implements Controller {
    @Override
    public void service(DataOutputStream dos, HttpRequest request, HttpResponse response) throws Exception {
        String body = request.getBody();
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");
        String email = params.get("email");
        MemoryUserRepository userDB = request.getUserDB();
        userDB.addUser(new User(userId, password, name, email));
        User existingUser = userDB.findUserById(userId);
        HttpResponse.writeRedirect(dos, Url.INDEX.path());
    }
    }
