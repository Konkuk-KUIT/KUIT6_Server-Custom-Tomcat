package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class HomeController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        response.forward("/index.html");
    }

}
