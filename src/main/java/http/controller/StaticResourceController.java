package main.java.http.controller;

import main.java.http.HttpRequest;
import main.java.http.HttpResponse;

import java.io.IOException;

public class StaticResourceController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        String resourcePath = "/".equals(path) ? "/index.html" : path;
        response.forward(resourcePath);
    }
}
