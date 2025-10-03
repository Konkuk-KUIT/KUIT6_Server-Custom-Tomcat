package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginController implements Controller{

    MemoryUserRepository memoryUserRepository;
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = httpRequest.getHttpBody();
        String url = httpRequest.getUrl();
        String[] queryParamArr = body.split("&");
        String userId = queryParamArr[0].split("=")[1];
        String userPw = queryParamArr[1].split("=")[1];
        User user = memoryUserRepository.findUserById(userId);
        if(user != null && userPw.equals(user.getPassword())) {
            httpResponse.redirect("/index.html", "logined=true; Path=/; HttpOnly", url);
            return;
        }
        httpResponse.redirect("/user/login_failed.html", null, url);
    }

    public void setMemoryUserRepository(MemoryUserRepository memoryUserRepository) {
        this.memoryUserRepository = memoryUserRepository;
    }
}
