package webserver;

import controller.*;
import http.request.HttpRequest;
import http.response.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    private Map<String, Controller> controllerMap = new HashMap<>();
    private HttpRequest req;
    private HttpResponse resp;

    public RequestMapper(HttpRequest req, HttpResponse resp) {
        this.req = req;
        this.resp = resp;

        // url-controller 매핑
        controllerMap.put(UrlPath.INDEX.getPath(), new ForwardController());
        controllerMap.put(UrlPath.SIGNUP.getPath(), new SignUpController());
        controllerMap.put(UrlPath.LOGIN.getPath(), new LoginController());
        controllerMap.put(UrlPath.LIST.getPath(), new ListController());
    }

    public void proceed() throws Exception {
        String url = req.getUrl();
        System.out.println("Requested URL: " + req.getUrl());
        // "/"는 index.html로 전환
        if (url.equals("/")) {
            url = "/index.html"; // "/" 요청을 index.html로 변환
        }

        Controller controller = controllerMap.get(url);

        if (controller == null) {
            // Default: ForwardController로 처리하거나 404 처리
            new ForwardController().execute(req, resp);
        } else {
            controller.execute(req, resp);
        }


    }
}
