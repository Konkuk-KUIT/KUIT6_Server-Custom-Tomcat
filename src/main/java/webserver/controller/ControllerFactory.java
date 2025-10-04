package webserver.controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerFactory {
    private static final Map<String, Controller> controllers = new HashMap<>();
    static {
        controllers.put("/", new HomeController());
        controllers.put("/index.html", new HomeController());
        controllers.put("/user/form.html", new UserFormController());
        controllers.put("/user/signup", new UserSignupController());
        controllers.put("/user/login.html", new UserLoginPageController());
        controllers.put("/user/login", new UserLoginController());
        controllers.put("/user/userList", new UserListController());
    }

    public static Controller getController(String path) {
        return controllers.get(path);
    }
}
