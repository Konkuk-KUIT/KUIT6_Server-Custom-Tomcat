package controller;

import enums.RequestPath;
import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getHeader().isLogined()) {
            response.forward(RequestPath.USER_LIST.getPath()+".html");
        } else {
            response.sendRedirect(RequestPath.LOGIN.getPath()+".html");
        }
    }
}