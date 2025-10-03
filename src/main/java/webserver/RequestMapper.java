package webserver;

import controller.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestMapper {
    Map<String, Controller> controllers = new HashMap<>();
    HttpRequest request;
    HttpResponse response;

    public RequestMapper(HttpRequest httpRequest, HttpResponse httpResponse) {
        this.request = httpRequest;
        this.response = httpResponse;
        mappingController();
    }

    private void mappingController() {
        insertSignInController();
        insertSignUpController();
        insertUserListController();
    }

    private void insertUserListController() {
        controllers.put(RequestRouter.USER_LIST_REQ.getPath(), new UserListController());
    }

    private void insertSignUpController() {
        controllers.put(RequestRouter.SIGN_UP_REQ.getPath(), new SignUpController());
    }

    private void insertSignInController() {
        controllers.put(RequestRouter.LOG_IN_REQ.getPath(), new SignInController());
    }

    public void proceed() throws IOException {
        Controller controller =  controllers.getOrDefault(request.getUrl(), new ForwardController());
        controller.execute(request, response);
        response.responseBody(request.getByteBody());
    }
}
