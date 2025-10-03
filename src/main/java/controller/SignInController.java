package controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class SignInController implements Controller {

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws IOException {
        String bodyData = httpRequest.getReqBody();
        String[] query = splitKeyAndValue(bodyData);
        User user = findUser(query[0].split("=")[1]);
        redirect(user, query[1].split("=")[1], response);
    }

    private void redirect(User user, String passwd, HttpResponse response) throws IOException {
        if(isLoggedInUser(user, passwd)) {
            response.response302Header("/", true);
        } else {
            response.response302Header("/user/login_failed.html");
        }
    }

    private String[] splitKeyAndValue(String reqBody) {
        return reqBody.split("&");
    }

    private User findUser(String userId) {
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        return memoryUserRepository.findUserById(userId);
    }

    private boolean isLoggedInUser(User user, String passwd) {
        return user != null && user.getPassword().equals(passwd);
    }
}
