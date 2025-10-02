package webserver;

import controller.*;
import enums.HttpMethod;
import enums.RequestPath;
import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    final Map<String, Controller> controllers = new HashMap<>();
    private final HttpRequest request;
    private final HttpResponse response;

    private static final Controller defaultController = new ForwardController();

    public RequestMapper(HttpRequest request, HttpResponse response) {
        this.request = request;
        this.response = response;
        initControllers();
    }

    // 매핑 초기화
    private void initControllers() {
        putControllers(HttpMethod.GET, RequestPath.ROOT, new HomeController());
        putControllers(HttpMethod.GET, RequestPath.SIGNUP, new SignUpController());
        putControllers(HttpMethod.POST, RequestPath.SIGNUP, new SignUpController());
        putControllers(HttpMethod.POST, RequestPath.LOGIN, new LoginController());
        putControllers(HttpMethod.GET, RequestPath.USER_LIST, new ListController());
    }

    private void putControllers(HttpMethod method, RequestPath path, Controller controller){
        controllers.put(method.getValue()+" "+path.getPath(), controller);
    }

    // 알맞은 컨트롤러 가져오고 만약 없으면 default 반환
    public void proceed() throws IOException {
        String url = request.getMethod().getValue()+" "+request.getUrl();
        Controller controller = controllers.getOrDefault(url,defaultController);
        controller.execute(request, response); // 컨트롤러 실행해줌
    }


}
