package webserver;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;
import http.enums.HttpMethod;
import http.enums.RequestPath;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private final Map<String, Controller> controllers;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        this.controllers = new HashMap<>();
        initializeControllers();
    }

    private void initializeControllers() {
        controllers.put(createKey(RequestPath.ROOT.getValue(), null), new ForwardController());
        controllers.put(createKey(RequestPath.USER_SIGNUP.getValue(), HttpMethod.POST), new UserSignupController());
        controllers.put(createKey(RequestPath.USER_LOGIN.getValue(), HttpMethod.POST), new UserLoginController());
        controllers.put(createKey(RequestPath.USER_LIST.getValue(), null), new UserListController());
    }

    private String createKey(String path, HttpMethod method) {
        if (method == null) {
            return path;
        }
        return path + "_" + method.name();
    }

    public void proceed() throws IOException {
        String path = httpRequest.getPath();
        HttpMethod method = httpRequest.getMethod();
        
        Controller controller = getController(path, method);
        if (controller == null) {
            controller = new ForwardController();
        }
        
        controller.execute(httpRequest, httpResponse);
    }

    private Controller getController(String path, HttpMethod method) {
        Controller controller = controllers.get(createKey(path, method));
        if (controller == null) {
            controller = controllers.get(path);
        }
        return controller;
    }
}