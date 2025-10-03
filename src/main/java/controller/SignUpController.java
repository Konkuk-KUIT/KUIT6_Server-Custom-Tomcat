package controller;

import db.MemoryUserRepository;
import enums.RequestPath;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

import java.io.IOException;
import java.util.Map;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        if (request.getMethod().isEqual("GET")) {
            // 회원가입 폼 페이지 보여주기
            response.forward("/user/form.html");
            return;
        }
        Map<String, String> params = request.getBody().getFormData();
        User user = new User(
                params.get("userId"),
                params.get("password"),
                params.get("name"),
                params.get("email")
        );

        MemoryUserRepository.getInstance().addUser(user);
        response.sendRedirect(RequestPath.INDEX.getPath());
    }
}
