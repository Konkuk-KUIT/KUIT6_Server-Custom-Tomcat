package webserver;

import controller.*;
import enums.HttpUrls;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    Map<String,Controller> controllers = new HashMap<>();
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;

    public RequestMapper(HttpRequest httpRequest,HttpResponse httpResponse) {
        this.httpRequest = httpRequest;
        this.httpResponse = httpResponse;
        putControllerAll();
    }

    public void proceed() throws IOException {
        final String method = httpRequest.getMethod();
        final String path = httpRequest.getPath();

        if ("GET".equals(method) && (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js"))) {
            new ForwardController().execute(httpRequest, httpResponse);
            return;
        }

        Controller controller = controllers.get(path);
        if (controller != null) {
            controller.execute(httpRequest, httpResponse);
            return;
        }

        if ("/".equals(path)) {
            new HomeController().execute(httpRequest, httpResponse);
            return;
        }
    }

    private void putControllerAll() {
        putController("/", new HomeController());
        putController(HttpUrls.LOGIN.getPath(), new LoginController());
        putController(HttpUrls.SIGNUP.getPath(), new SignUpController());
        putController(HttpUrls.USERLIST.getPath(), new ListController());
    }

    private void putController(String path, Controller controller) {
        controllers.put(path, controller);
    }
}
