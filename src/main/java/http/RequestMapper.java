package http;

import http.controller.*;
import webserver.URL;

import java.io.IOException;
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
        if(request.getMethod().equals("get") && request.getUrl().endsWith(".html")) {
           this.controller = controllerMap.get("forward");
        }else this.controller = controllerMap.get(request.getUrl());
    }

    private void initializeMap(){
        controllerMap.put("forward",new ForwardController());
        controllerMap.put(URL.DEFAULT.getUrl(),new HomeController());
        controllerMap.put(URL.INDEX.getUrl(),new HomeController());
        controllerMap.put(URL.USER_SIGNUP.getUrl(),new SignUpController());
        controllerMap.put(URL.USER_LOGIN.getUrl(),new LoginController());
        controllerMap.put(URL.USER_USERLIST.getUrl(),new ListController());
    }

    public void proceed() throws IOException {
        controller.execute(request, response);
    }
}
