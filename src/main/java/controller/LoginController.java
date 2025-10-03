package controller;

import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpRequestUtils;
import http.util.HttpResponse;
import http.util.UrlPath;
import model.QueryKey;
import model.User;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

public class LoginController implements Controller {
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception{
        String postRequest = httpRequest.getPostRequest();
        postLogin(postRequest, httpResponse);
    }

    private void postLogin(String postRequest, HttpResponse httpResponse) throws IOException {
        Map<String, String> userInfoMap = HttpRequestUtils.parseQueryParameter(postRequest);

        String id  = userInfoMap.get(QueryKey.userId.name());
        String password = userInfoMap.get(QueryKey.password.name());


        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        User userById = memoryUserRepository.findUserById(id);

        if(userById != null && userById.getPassword().equals(password)){
            httpResponse.response302Cookie(UrlPath.HOME.getPath(), "logined=true");
        }else{
            httpResponse.response302Header(UrlPath.LOGIN_FAILED.getPath());
        }
    }
}
