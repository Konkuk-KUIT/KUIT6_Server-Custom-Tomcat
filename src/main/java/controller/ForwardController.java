package controller;

import enums.HtmlUrls;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class ForwardController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpResponse.forward(httpRequest.getPath());
    }
}
