package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public interface Controller {

    //들어온 url을 실행하는 역할
    void execute(HttpRequest request, HttpResponse response) throws IOException;
}
