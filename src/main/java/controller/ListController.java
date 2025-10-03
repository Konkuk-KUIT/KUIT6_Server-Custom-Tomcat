package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;

public class ListController implements Controller {
    private final MemoryUserRepository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        String cookie = request.getHeader("Cookie");
        if (cookie == null || !cookie.contains("logined=true")) {
            response.sendRedirect("/user/login.html");
            return;
        }

        response.forward("/user/list.html");
    }
}
