package controller;

import db.MemoryUserRepository;
import db.Repository;
import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import model.UserQueryKey;
import webserver.UrlPath;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignUpController implements Controller {
    private static final Logger log = Logger.getLogger(SignUpController.class.getName());
    private final Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        HttpMethod method = request.getMethod();
        if (method != HttpMethod.GET && method != HttpMethod.POST) {
            response.send404(request.getPath());
            return;
        }

        String userId = request.getParameter(UserQueryKey.USER_ID.key());
        if (userId == null || userId.isEmpty()) {
            response.response302Header(UrlPath.INDEX.value());
            return;
        }

        User user = new User(
                userId,
                request.getParameter(UserQueryKey.PASSWORD.key()),
                request.getParameter(UserQueryKey.NAME.key()),
                request.getParameter(UserQueryKey.EMAIL.key()));
        repository.addUser(user);
        log.log(Level.INFO, "User signup method={0} userId={1}", new Object[]{method, user.getUserId()});
        response.response302Header(UrlPath.INDEX.value());
    }
}