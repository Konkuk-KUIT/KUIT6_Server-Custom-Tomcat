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
        System.out.println(httpRequest.getRequestURI());
        if(httpRequest.getRequestURI().endsWith(".html")){
            controller = new ForwardController();
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
