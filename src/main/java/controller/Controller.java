package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

public interface Controller {
    void process(HttpRequest request, HttpResponse response);
}