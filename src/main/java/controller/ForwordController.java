package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.logging.Logger;

public class ForwordController implements Controller {
    private static final Logger log = Logger.getLogger(ForwordController.class.getName());

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        response.forward(request.getPath());
    }
}
