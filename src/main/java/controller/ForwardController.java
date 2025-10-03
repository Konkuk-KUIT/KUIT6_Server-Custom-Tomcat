package controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ForwardController implements Controller {

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws IOException {
        byte[] body = httpRequest.getByteBody();
        if(httpRequest.isCss()) {
            response.response200Header(body.length, true);
        } else {
            response.response200Header(body.length);
        }
    }


}
