package webserver.controller;

import db.MemoryUserRepository;
import db.Repository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.enums.HttpStatus;

import java.nio.file.Files;
import java.nio.file.Paths;

public class LoginController implements Controller {
    private final Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String userId = httpRequest.getParam("userId");
        String password = httpRequest.getParam("password");
        System.out.println("password = " + password);
        System.out.println("userId = " + userId);
        User userById = repository.findUserById(userId);
        System.out.println("userById.getName() = " + userById.getName());
        boolean isCorrectPassword = userById.getPassword().equals(password);

        if(isCorrectPassword){
            httpResponse.setStatus(HttpStatus.FOUND_302);
            httpResponse.setHeader("Location", "/index.html");
            httpResponse.setHeader("Set-Cookie", "logined=true");
            httpResponse.setBody();
        } else {
            byte[] body = Files.readAllBytes(Paths.get("./webapp/user/login_failed.html"));

            httpResponse.setStatus(HttpStatus.OK_200);
            httpResponse.setHeader("Content-Type", "text/html; charset=utf-8");
            httpResponse.setHeader("Content-Length", String.valueOf(body.length));
            httpResponse.setBody();
        }


    }
}
