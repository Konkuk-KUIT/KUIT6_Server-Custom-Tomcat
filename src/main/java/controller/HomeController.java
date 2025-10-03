package controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

public class HomeController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        try{
            response.forward("/index.html");

        } catch (IOException e) {
            throw new RuntimeException("Load Fail..",e);
        }
    }
}
