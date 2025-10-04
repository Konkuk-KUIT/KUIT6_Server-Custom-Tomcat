package http.controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

public interface Controller {
    public void execute(HttpRequest request, HttpResponse response) throws IOException;
}
