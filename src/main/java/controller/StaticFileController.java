package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;

public class StaticFileController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();

        if (path.equals("/")) {
            path = "/index.html";
        }

        response.forward(path);
    }
}