package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;
import http.util.UrlPath;

public class CssController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        httpResponse.responseCss(UrlPath.ROOT.getPath() + httpRequest.getRequestURI());
    }
}
