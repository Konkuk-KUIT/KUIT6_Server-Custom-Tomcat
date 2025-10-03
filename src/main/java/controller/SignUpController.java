package controller;

import constant.HttpMethod;
import constant.HttpStatusCode;
import constant.Url;
import constant.UserQueryKey;
import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.util.HttpRequestUtils;
import model.User;

import java.util.Map;

public class SignUpController implements Controller {
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws Exception {
        Map<String, String> params = null;

        if (request.getMethod() == HttpMethod.GET) {
            String queryString = request.getQueryString();
            if (!queryString.isEmpty()) {
                params = HttpRequestUtils.parseQueryParameter(queryString);
            }
        } else if (request.getMethod() == HttpMethod.POST) {
            params = HttpRequestUtils.parseQueryParameter(request.getBody());
        }


        if (params != null) {
            User user = new User(
                    params.get(UserQueryKey.USER_ID.getKey()),
                    params.get(UserQueryKey.PASSWORD.getKey()),
                    params.get(UserQueryKey.NAME.getKey()),
                    params.get(UserQueryKey.EMAIL.getKey())
            );
            MemoryUserRepository.getInstance().addUser(user);
        }

        // 회원가입 끝나면 index.html 로 redirect
        response.response302Header(Url.INDEX.getPath());
    }

}
