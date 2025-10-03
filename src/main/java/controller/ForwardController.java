package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;
import http.util.UrlPath;

public class ForwardController implements Controller {
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception{
        httpResponse.forward(UrlPath.ROOT.getPath() + httpRequest.getRequestURI());
    }
}
