package webserver;

import controller.*;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("POST /user/signup", new CreateUserController());
        controllers.put("POST /user/login", new LoginController());

        controllers.put("GET /user/form.html", new ForwardController("/user/form.html"));
        controllers.put("GET /user/login.html", new ForwardController("/user/login.html"));
        controllers.put("GET /user/userList", new ListUserController());
    }

    public static Controller getController(String method, String path) {
        return controllers.get(method + " " + path);
    }
}