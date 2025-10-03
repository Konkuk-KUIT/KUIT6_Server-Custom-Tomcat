package controller;

import http.HttpRequest;
import http.HttpResponse;
import webserver.UrlPath;

import java.io.IOException;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String logined = request.getCookie("logined");
        if (!"true".equalsIgnoreCase(logined)) {
            response.response302Header(UrlPath.USER_LOGIN_PAGE.value());
            return;
        }

        String path = request.getPath();
        String resource = "user/list.html";
        if (UrlPath.USER_LIST.value().equals(path) || UrlPath.USER_LIST_HTML.value().equals(path)) {
            resource = path.startsWith("/") ? path.substring(1) : path;
        }
        response.forward(resource);
    }
}