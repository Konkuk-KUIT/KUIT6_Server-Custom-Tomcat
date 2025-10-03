package webserver;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest request;
    private final HttpResponse response;
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/", new HomeController());
        controllers.put("/index.html", new ForwardController());
        controllers.put("/user/signup", new SignUpController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/userList", new ListController());
    }

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public void proceed() throws Exception {
        String url = request.getUrl();

        Controller controller = controllers.get(url);

        if (controller == null) {
            controller = new ForwardController();
        }

        controller.execute(request, response);
    }
}
