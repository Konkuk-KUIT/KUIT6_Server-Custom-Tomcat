package webserver.controller;

import db.MemoryUserRepository;
import db.Repository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.enums.HttpStatus;

public class SignUpController implements Controller {
    private final Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String userId = httpRequest.getParam("userId");
        String password = httpRequest.getParam("password");
        String name = httpRequest.getParam("name");
        String email = httpRequest.getParam("email");
        repository.addUser(new User(userId, password, name, email));

        httpResponse.setStatus(HttpStatus.FOUND_302);
        httpResponse.setHeader("Location", "/index.html");
        httpResponse.setBody();
    }
}
