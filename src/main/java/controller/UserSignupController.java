package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.enums.HttpMethod;
import http.enums.RequestPath;
import http.util.HttpRequestUtils;
import model.User;
import model.UserField;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserSignupController implements Controller {
    private static final Logger log = Logger.getLogger(UserSignupController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        Map<String, String> params;

        // body에서 파라미터 추출
        params = HttpRequestUtils.parseQueryParameter(request.getBody());
        log.log(Level.INFO, "POST Signup params: " + params);

        // User 객체 생성
        User newUser = new User(
                params.get(UserField.USER_ID.getValue()),
                params.get(UserField.PASSWORD.getValue()),
                params.get(UserField.NAME.getValue()),
                params.get(UserField.EMAIL.getValue())
        );

        // 메모리 저장소에 저장
        MemoryUserRepository repository = MemoryUserRepository.getInstance();
        repository.addUser(newUser);
        log.log(Level.INFO, "New User Registered: " + newUser.getUserId());

        // 302 리다이렉트로 메인 페이지로 이동
        response.redirect(RequestPath.INDEX.getValue());
    }
}