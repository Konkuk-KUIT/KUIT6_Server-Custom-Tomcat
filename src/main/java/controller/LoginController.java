package controller;

import enums.HtmlUrls;
import enums.UserKey;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

import static db.MemoryUserRepository.getInstance;
import static http.util.HttpRequestUtils.parseQueryParameter;

public class LoginController implements Controller {

    public LoginController() {}

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> userString = parseQueryParameter(httpRequest.getHttpBody());
        if(getInstance().findUserById(userString.get(UserKey.USER_ID.getKey())) == null){
            httpRequest.setPath(HtmlUrls.LOGIN_FAIL.getPath());
            httpResponse.redirect(httpRequest.getPath(), false);
        } else {
            httpRequest.setPath(HtmlUrls.INDEX.getPath());
            httpResponse.redirect(httpRequest.getPath(), true);
        }
    }
}
