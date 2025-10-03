package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

public interface Controller {
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
