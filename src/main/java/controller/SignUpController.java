package controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;

public class SignUpController implements Controller {

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse response) throws IOException {
        String bodyData = httpRequest.getReqBody();
        String[] query = splitKeyAndValue(bodyData);
        insertToRepo(query[0].split("=")[1],
                query[1].split("=")[1],
                query[2].split("=")[1],
                query[3].split("=")[1]);
        response.response302Header("/");
    }

    private String[] splitKeyAndValue(String reqBody) {
        return reqBody.split("&");
    }

    private void insertToRepo(String userId, String password, String name, String email) {
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        User user = new User(userId, password, name, email);
        memoryUserRepository.addUser(user);
    }
}
