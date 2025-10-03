package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;

public class RequestMapper {
    HttpRequest httpRequest;
    HttpResponse httpResponse;
    Controller controller;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        requestMapping();
    }

    private void requestMapping() {
        if(httpRequest.getRequestURI().endsWith(".html")){
            controller = new ForwardController();
            return;
        }

        if(httpRequest.getRequestURI().endsWith(".css")){
            controller = new CssController();
            return;
        }

        controller = ControllerMap.getController(httpRequest.getRequestURI());
    }

    public void proceed() {
        try {
            controller.execute(httpRequest, httpResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
