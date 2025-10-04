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

    public Controller findController(String rawUrl) {
//        if (url == null) {
//            return defaultController;
//        }
//        Controller controller = controllers.get(url);
//        if (controller != null) {
//            return controller;
//        }
//        if (url.endsWith(".html")) {
//            return defaultController;
//        }
        String url = normalize(rawUrl);              // "/user/userlist" 같은 형태로

        // 별칭 → 표준 경로로 rewrite
        if ("/user/userlist".equals(url))      url = "/user/list";
        if ("/user/userlist.html".equals(url)) url = "/user/list.html";

        Controller c = controllers.get(url);
        if (c != null) return c;

        // 확장자 없는 경로일 때 *.html 매핑도 시도
        if (!url.contains(".")) {
            c = controllers.get(url + ".html");
            if (c != null) return c;
        }

        return defaultController;
    }

    private String normalize(String url) {
        if (url == null || url.isEmpty()) return "/";
        int q = url.indexOf('?'); if (q >= 0) url = url.substring(0, q);
        if (!url.startsWith("/")) url = "/" + url;
        if (url.endsWith("/") && url.length() > 1) url = url.substring(0, url.length()-1);
        return url.toLowerCase();                 // 대소문자 흔들림 방지
    }
}
