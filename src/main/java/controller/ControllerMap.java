package controller;

import java.util.HashMap;
import java.util.Map;

public class ControllerMap {
    private static final Map<String, Controller> controllerMap =  new HashMap<String, Controller>();

    static {
        controllerMap.put("/user/login", new LoginController());
        controllerMap.put("forward", new ForwardController());
        controllerMap.put("/", new HomeController());
        controllerMap.put("/user/userList", new ListController());
        controllerMap.put("/user/signup", new SignUpController());
        controllerMap.put("css", new CssController());
    }
    public static Controller getController(String url){
        return controllerMap.get(url);
    }
}
