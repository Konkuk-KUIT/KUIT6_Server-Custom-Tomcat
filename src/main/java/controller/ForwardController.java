package controller;

import enums.RequestPath;
import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getUrl();

        if (RequestPath.ROOT.matches(path)) {
            path = RequestPath.INDEX.getPath();
        }

        try {
            response.forward(path);
        } catch (IOException e) {
            response.sendNotFound();
        }
    }
}
