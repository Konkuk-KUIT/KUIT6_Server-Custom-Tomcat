package webserver;

import controller.Controller;
import controller.ForwardController;
import http.HttpRequest;
import http.HttpResponse;
import http.constants.HttpMethod;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {

    private final HttpRequest httpRequest;
    private final HttpResponse httpResponse;
    private final BufferedReader br;
    private Controller controller;

    // exact path → Controller 매핑
    private final Map<String, Controller> routes = new HashMap<>();


    public RequestMapper(HttpRequest req, HttpResponse res, BufferedReader br) {
        this.httpRequest = req;
        this.httpResponse = res;
        this.br = br;
        registerRoutes();
    }

    private void registerRoutes() {
        // 당신이 이미 가지고 있는 컨트롤러들 (예: HomeController, SignUpController, LoginController, ListController 등)
        routes.put("/", new controller.HomeController());

        routes.put("/user/signup", new controller.SignUpController());
        routes.put("/user/login",  new controller.LoginController());

        // /user/userList : JSON 등 서버 사이드 목록 API라면 인증 불필요(예시로 그대로 사용)
        routes.put("/user/userList", new controller.ListController(br));
    }

    public void proceed() {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        String url = httpRequest.getUrl();

        if (httpMethod.toString().equals("GET") && url.endsWith(".html")) {
            controller = new ForwardController();
            controller.execute(httpRequest, httpResponse);
            return;
        }
        routes.get(httpRequest.getUrl()).execute(httpRequest, httpResponse);
    }

}
