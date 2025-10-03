package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.HashMap;

public class UserListController implements Controller {

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws IOException {
        HashMap<String, String> map = httpRequest.getCookies();
        if(!isLogined(map)) {
            response.response302Header("/user/login.html");
        }
    }

    private boolean isLogined(HashMap<String, String> map) {
        if(map.containsKey("logined")) {
            return Boolean.parseBoolean(map.get("logined"));
        }
        return false;
    }
}
