package http;

import http.controller.*;
import webserver.URL;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private static final Map<String, Controller> controllerMap = new HashMap<>();
    private final Controller controller;
    private final HttpRequest request;
    private final HttpResponse response;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        initializeMap();
        this.request = request;
        this.response = response;
        if(request.getMethod().equals("GET") && request.getUrl().endsWith(".html")) {
           this.controller = controllerMap.get("forward");
        }else this.controller = controllerMap.get(request.getUrl());
    }

    private void initializeMap(){
        controllerMap.put("forward",new ForwardController());
        controllerMap.put(URL.DEFAULT.getUrl(),new HomeController());
        controllerMap.put(URL.DEFAULT.getUrl(),new SignUpController());
        controllerMap.put(URL.DEFAULT.getUrl(),new LoginController());
        controllerMap.put(URL.DEFAULT.getUrl(),new ListController());
    }

    public void proceed(){
        controller.execute(request, response);
    }
}
