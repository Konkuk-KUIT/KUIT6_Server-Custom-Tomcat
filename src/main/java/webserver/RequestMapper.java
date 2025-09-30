package webserver;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;
import http.enums.HttpMethod;

import java.io.IOException;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;

    private static final Map<String, Controller> CONTROLLERS = WebConfig.configureControllers();

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
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
        // 먼저 path + method 조합으로 찾기
        String key = WebConfig.createKey(path, method);
        Controller controller = CONTROLLERS.get(key);

        // 없으면 path만으로 찾기
        if (controller == null) {
            controller = CONTROLLERS.get(path);
        }

        return controller;
    }
}