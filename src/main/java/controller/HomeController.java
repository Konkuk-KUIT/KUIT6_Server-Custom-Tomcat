package controller;

import constant.Url;
import http.HttpRequest;
import http.HttpResponse;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        response.response200(Url.INDEX.getPath());
    }
}
