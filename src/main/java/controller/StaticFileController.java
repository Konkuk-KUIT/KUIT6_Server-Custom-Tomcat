package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;
import http.util.Endpoints;

public class StaticFileController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        if (path.equals(Endpoints.ROOT.getPath())) {
            response.forward(Endpoints.INDEX.getPath());
            return;
        }
        response.forward(path);
    }
}