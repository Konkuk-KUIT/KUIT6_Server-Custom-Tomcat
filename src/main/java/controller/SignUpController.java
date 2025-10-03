package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class SignUpController implements Controller{

    MemoryUserRepository memoryUserRepository;
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        String body = httpRequest.getHttpBody();
        String url = httpRequest.getUrl();
        String[] queryParamArr = body.split("&");
        String userId = queryParamArr[0].split("=")[1];
        String userPw = queryParamArr[1].split("=")[1];
        String userName = queryParamArr[2].split("=")[1];
        String userEmail = queryParamArr[3].split("=")[1];
        User user = new User(userId, userPw, userName, userEmail);
        memoryUserRepository.addUser(user);
        httpResponse.redirect("/index.html", null, url);
    }

    public void setMemoryUserRepository(MemoryUserRepository memoryUserRepository) {
        this.memoryUserRepository = memoryUserRepository;
    }
}
