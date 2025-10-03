package webserver;

import controller.Controller;
import controller.ForwardController;
import controller.HomeController;
import controller.ListController;
import controller.LoginController;
import controller.SignUpController;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final Map<String, Controller> controllers = new HashMap<>();
    private final Controller defaultController = new ForwardController();

    public RequestMapper() {
        // controllers.put(UrlPath.ROOT.value(), new HomeController());
        HomeController homeController = new HomeController();
        controllers.put(UrlPath.ROOT.value(), homeController);
        controllers.put(UrlPath.INDEX.value(), homeController);
        controllers.put(UrlPath.USER_SIGNUP.value(), new SignUpController());
        controllers.put(UrlPath.USER_LOGIN.value(), new LoginController());
        controllers.put(UrlPath.USER_LIST.value(), new ListController());
        controllers.put(UrlPath.USER_LIST_ALIAS.value(), new ListController());
        controllers.put(UrlPath.USER_LIST_HTML.value(), new ListController());
    }

    public Controller findController(String url) {
        if (url == null) {
            return defaultController;
        }
        Controller controller = controllers.get(url);
        if (controller != null) {
            return controller;
        }
        if (url.endsWith(".html")) {
            return defaultController;
        }
        return defaultController;
    }
}
