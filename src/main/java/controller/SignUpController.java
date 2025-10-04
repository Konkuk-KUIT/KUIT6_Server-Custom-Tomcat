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

        // Requirement 2 : GET 방식으로 회원가입하기
//        if (method != HttpMethod.GET && method != HttpMethod.POST) {
//                response.send404(request.getPath());
//                return;
//        }

        // Requirement 3 : POST 방식으로 회원가입하기
        if (method == HttpMethod.GET) {
            response.forward("user/form.html");
            return;
        }

        if (method != HttpMethod.POST) {
            response.send404(request.getPath());
            return;
        }




        String userId = normalized(request, UserQueryKey.USER_ID);
        String password = normalized(request, UserQueryKey.PASSWORD);
        String name = normalized(request, UserQueryKey.NAME);
        String email = normalized(request, UserQueryKey.EMAIL);

        if (userId.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            log.log(Level.WARNING, "Missing sign-up parameters method={0} userId={1}", new Object[]{method, userId});
            response.response302Header(UrlPath.USER_SIGNUP_FORM.value());
            return;
        }

        User user = new User(
                userId,
                password,
                name,
                email);
        repository.addUser(user);
        log.log(Level.INFO, "User signup method={0} userId={1}", new Object[]{method, user.getUserId()});
        response.response302Header(UrlPath.INDEX.value());
    }

    private String normalized(HttpRequest request, UserQueryKey key) {
        String value = request.getParameter(key.key());
        return value == null ? "" : value.trim();
    }
}
