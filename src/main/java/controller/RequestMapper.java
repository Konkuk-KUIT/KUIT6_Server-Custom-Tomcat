package controller;

import http.constant.ApiPath;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private static final Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put(ApiPath.SIGNUP.getPath(), new SignupController());
        controllers.put(ApiPath.LOGIN.getPath(), new LoginController());
        controllers.put(ApiPath.USER_LIST.getPath(), new UserListController());
    }

    private final HttpRequest request;
    private final HttpResponse response;

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public void proceed() throws IOException {
        String path = request.getPath();

        // URL에 매핑된 Controller 찾기
        Controller controller = controllers.getOrDefault(path, new StaticFileController());

        // Controller 실행
        controller.execute(request, response);
    }
}