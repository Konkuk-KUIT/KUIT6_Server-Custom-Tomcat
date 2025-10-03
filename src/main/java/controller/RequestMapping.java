package controller;

import http.util.Endpoints;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        // 각 URL 경로에 맞는 컨트롤러를 미리 매핑해 둡니다.
        controllers.put(Endpoints.USER_SIGNUP.getPath(), new UserCreateController());
        controllers.put(Endpoints.USER_LOGIN.getPath(), new UserLoginController());
        controllers.put(Endpoints.USER_LIST_ALIAS.getPath(), new UserListController());
    }

    public static Controller getController(String path) {
        // 매핑된 컨트롤러가 있으면 반환하고, 없으면 정적 파일을 처리하는 기본 컨트롤러를 반환합니다.
        return controllers.getOrDefault(path, new StaticFileController());
    }
}