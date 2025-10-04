package controller;

import enums.HtmlUrls;
import enums.HttpUrls;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        httpRequest.setPath(httpRequest.getHttpHeader());
        if(httpRequest.getPath() == HtmlUrls.USERLIST.getPath()){
            httpResponse.redirect(httpRequest.getPath(), true);
        } else {
            httpResponse.forward(httpRequest.getPath());
        }
    }
}
