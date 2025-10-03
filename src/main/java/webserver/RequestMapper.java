package webserver;

import controller.*;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest request;
    private final HttpResponse response;

    private final Map<String, Controller> controllerMap = new HashMap<>();

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;

        controllerMap.put("/user/signup", new SignUpController());
        controllerMap.put("/user/login", new LoginController());
        controllerMap.put("/user/userList", new UserListController());
    }

    public void proceed() throws IOException {
        String path = request.getPath();

        Controller controller = controllerMap.get(path);

        if (controller == null) {
            controller = new StaticFileController();
        }

        controller.execute(request, response);
    }
}
