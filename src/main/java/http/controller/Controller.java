package http.controller;

import http.HttpRequest;
import http.HttpResponse;

public interface Controller {
    public void execute(HttpRequest request, HttpResponse response);
}
