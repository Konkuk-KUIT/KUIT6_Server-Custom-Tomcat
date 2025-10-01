package controller;

import constant.Url;
import constant.UserQueryKey;
import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.util.Map;

public class LoginController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> params = HttpRequestUtils.parseQueryParameter(request.getBody());
        if (params == null) {
            response.response302Header(Url.USER_LOGIN_HTML.getPath());
            return;
        }

        String userId = params.get(UserQueryKey.USER_ID.getKey());
        String password = params.get(UserQueryKey.PASSWORD.getKey());

        User user = MemoryUserRepository.getInstance().findUserById(userId);

        if (user != null && user.getPassword().equals(password)) {
            response.response302WithCookie(Url.INDEX.getPath(), "logined=true"); // ✅ 성공
        } else {
            response.response302Header(Url.USER_LOGIN_FAILED.getPath()); // ✅ 실패
        }
    }
}
