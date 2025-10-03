package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class SignUpController implements Controller {
    private final MemoryUserRepository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        User user = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email")
        );
        repository.addUser(user);
        response.sendRedirect("/index.html");
    }
}
