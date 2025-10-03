package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

public class ForwardController implements Controller {
    private final String forwardPath;

    public ForwardController(String forwardPath) {
        this.forwardPath = forwardPath;
    }

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        response.forward(forwardPath);
    }
}