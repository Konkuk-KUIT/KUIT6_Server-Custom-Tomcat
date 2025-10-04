package controller;

import enums.HtmlUrls;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class HomeController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
       httpRequest.setPath(HtmlUrls.INDEX.getPath());
       httpResponse.forward(httpRequest.getPath());
    }
}
