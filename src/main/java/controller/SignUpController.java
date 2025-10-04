package controller;

import enums.HtmlUrls;
import enums.UserKey;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static db.MemoryUserRepository.getInstance;
import static http.util.HttpRequestUtils.parseQueryParameter;

public class SignUpController implements Controller {
    public SignUpController() {}

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        createUser(httpResponse, httpRequest.getHttpBody());
    }

    private static void createUser(HttpResponse httpResponse, String query) throws IOException {
        Map<String, String> userString = parseQueryParameter(query);
        User user = new User(userString.get(UserKey.USER_ID.getKey()), userString.get(UserKey.PASSWORD.getKey()), userString.get(UserKey.NAME.getKey()), userString.get(UserKey.EMAIL.getKey()));
        getInstance().addUser(user);
        httpResponse.redirect(HtmlUrls.INDEX.getPath(), false);
    }
}
