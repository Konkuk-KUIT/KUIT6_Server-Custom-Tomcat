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
        Map<String,String> params = request.getMethod().isEqual("GET")
                ? request.getStartLine().getQueryParams() // GET 방식이면 query string 가져오고
                : request.getBody().getFormData(); // POST면 요청 Body에서 파라미터 읽음

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
