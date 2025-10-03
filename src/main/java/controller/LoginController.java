package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginController implements Controller {
    private final MemoryUserRepository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");

        User user = repository.findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            response.sendRedirectWithCookie("/index.html", "logined=true");
        } else {
            response.sendRedirect("/user/login_failed.html");
        }
    }
}
