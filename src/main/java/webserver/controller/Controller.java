package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

public interface Controller {
    void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception;
}
