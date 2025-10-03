package webserver;

import controller.Controller;
import controller.LoginController;
import controller.SignUpController;
import controller.UserListController;

import java.util.HashMap;
import java.util.Map;

import static enumclasses.URL.*;

public class RequestMapper {
    // url, controller가 key-value 형태로 저장
    private final Map<String, Controller> controllers = new HashMap<>();
    private final HttpRequest request;
    private final HttpResponse response;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        registerControllers();
    }

    private void registerControllers() {
        controllers.put(USERLIST.URL, new UserListController());
        controllers.put(LOGIN.URL, new LoginController());
        controllers.put(SIGNUP.URL, new SignUpController());
    }

    public void proceed() {
        String path = request.getPath();
        Controller controller = controllers.get(path);
        if (controller != null) {
            controller.service(request, response);
            return;
        }
        response.forward(path);
    }
}
