package webserver;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private final HttpRequest request;
    private final HttpResponse response;

    // URL → Controller 매핑 테이블
    private static final Map<String, Controller> mappings = new HashMap<>();

    static {
        mappings.put("/", new HomeController());
        mappings.put("/index.html", new ForwardController());
        mappings.put("/user/signup", new SignUpController());
        mappings.put("/user/login", new LoginController());
        mappings.put("/user/userList", new ListController());
    }

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
    }

    public void proceed() throws Exception {
        String path = request.getPath();
        Controller controller = mappings.getOrDefault(path, new ForwardController());
        // 매핑 X -> 정적 컨텐츠 반환
        controller.execute(request, response);
    }

}
