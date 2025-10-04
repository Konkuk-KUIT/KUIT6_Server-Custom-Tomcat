package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

public interface Controller {
    void execute(HttpRequest req, HttpResponse resp) throws Exception;
}

