//특정 URL 파일을 그대로 클라이언트에 보여주는 역할
package controller;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest req, HttpResponse resp) throws Exception {
        String path = req.getUrl();

        if (path.equals("/")) {
            path = "/index.html";
        }
        resp.forward(path);
    }
}

