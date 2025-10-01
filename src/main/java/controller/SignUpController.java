package controller;

import db.MemoryUserRepository;
import http.util.*;
import model.User;

import java.util.Map;

public class SignUpController implements Controller {
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception{

        if(isGetSignup(httpRequest)){
            signUpGetRepository(httpRequest.getRequestURI());
        }

        if(isPostSignup(httpRequest)) {
            String postRequest = httpRequest.getPostRequest();
            signUpPostRepository(postRequest);
        }
        httpResponse.response302Header(UrlPath.HOME.getPath());
    }

    private static boolean isPostSignup(HttpRequest httpRequest) {
        return httpRequest.getMethod().equals(HttpMethod.POST) && httpRequest.getRequestURI().equals("/user/signup");
    }

    private boolean isGetSignup(HttpRequest httpRequest) {
        return httpRequest.getMethod().equals(HttpMethod.GET) && isSignUp(httpRequest.getRequestURI());
    }

    private boolean isSignUp(String requestURI) {
        return requestURI.contains("/user/signup?");
    }
    private void signUpGetRepository( String requestURI) {
        String query = requestURI.split("\\?",2)[1];
        Map<String, String> userInfoMap = HttpRequestUtils.parseQueryParameter(query);

        User user = User.factory(userInfoMap);
        saveUser(user);
    }
    private void saveUser(User user) {
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        memoryUserRepository.addUser(user);
    }
    private void signUpPostRepository(String postRequest) {
        Map<String, String> userInfoMap = HttpRequestUtils.parseQueryParameter(postRequest);

        User user = User.factory(userInfoMap);
        saveUser(user);
    }
}
