package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.enums.HttpStatus;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        byte[] body = Files.readAllBytes(Paths.get("./webapp"+httpRequest.getPath()));

        httpResponse.setStatus(HttpStatus.OK_200);
        httpResponse.setHeader("Content-Type", "text/html; charset=utf-8");
        httpResponse.setHeader("Content-Length", String.valueOf(body.length));
        httpResponse.setBody(body);

    }
}
