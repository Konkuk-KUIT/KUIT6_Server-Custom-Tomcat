package controller;

import enums.HttpHeader;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class ListController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {

        String cookie = request.getHeaders().get(HttpHeader.COOKIE.value());

        if(cookie == null) {
            response.redirect("/user/login.html", null);
        }else if(cookie.equals("logined=true")) {
            response.redirect("/user/list.html", null);
        }

    }
}
