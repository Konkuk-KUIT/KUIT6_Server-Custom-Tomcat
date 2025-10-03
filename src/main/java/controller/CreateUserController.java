package controller;

import db.MemoryUserRepository;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;
import java.util.logging.Logger;

public class CreateUserController implements Controller {
    private static final Logger log = Logger.getLogger(CreateUserController.class.getName());

    @Override
    public void process(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email")
        );
        MemoryUserRepository.getInstance().addUser(user);
        log.info("New User Created: " + user);
        response.sendRedirect("/index.html");
    }
}