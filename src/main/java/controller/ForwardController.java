package controller;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForwardController implements Controller {
    private static final Logger log = Logger.getLogger(ForwardController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        if (path == null || path.isEmpty() || "/".equals(path)) {
            path = "index.html";
        } else if (path.startsWith("/")) {
            path = path.substring(1);
        }
        log.log(Level.INFO, "Forward static resource path={0}", path);
        response.forward(path);
    }
}
