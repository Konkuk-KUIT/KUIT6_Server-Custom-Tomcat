package webserver;

import controller.*;
import enums.HttpMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    HttpRequest request;
    HttpResponse response;
    Map<String, Controller>  controllers = new HashMap<>();

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.request = httpRequest;
        this.response = httpResponse;

        controllers.put("/", new HomeController());
        controllers.put("/user/signup", new SignUpController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/userList", new ListController());
    }

    public void proceed() throws IOException {

        Controller controller = controllers.get(request.getUrl());

        if(controller == null) {
            controller = new ForwardController();
        }

        controller.execute(request, response);
    }
}
