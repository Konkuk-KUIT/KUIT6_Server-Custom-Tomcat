package controller;

import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpResponse;
import model.User;
import model.UserFormKey;
import http.util.Endpoints;

public class UserCreateController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getParameter(UserFormKey.USER_ID.getKey()),
                request.getParameter(UserFormKey.PASSWORD.getKey()),
                request.getParameter(UserFormKey.NAME.getKey()),
                request.getParameter(UserFormKey.EMAIL.getKey()));
        MemoryUserRepository.getInstance().addUser(user);

        response.redirect(Endpoints.INDEX.getPath());
    }
}