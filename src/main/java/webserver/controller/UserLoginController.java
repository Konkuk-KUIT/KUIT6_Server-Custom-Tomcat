package webserver.controller;

import constant.Url;
import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.DataOutputStream;
import java.util.Map;

public class UserLoginController implements Controller {
    @Override
    public void service(DataOutputStream dos, HttpRequest request, HttpResponse response) throws Exception {
        String method = request.getMethod();
        String body = request.getBody();
        MemoryUserRepository userDB = request.getUserDB();
        if (method.equals("POST")) {
            Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

            String userId = params.get("userId");
            String password = params.get("password");

            User existingUser = userDB.findUserById(userId);
            // 로그인 검증
            if (existingUser != null) {
                // 로그인 성공
                HttpResponse.writeRedirectWithCookie(dos, Url.INDEX.path(), "logined=true");
            } else {
                // 로그인 실패
                HttpResponse.writeRedirect(dos,Url.USER_LOGIN_FAILED.path());
            }
        }
    }
}
