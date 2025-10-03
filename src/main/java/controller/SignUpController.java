package controller;

import db.MemoryUserRepository;
import enums.HttpMethod;
import http.util.HttpRequestUtils;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.IOException;
import java.util.Map;

public class SignUpController implements Controller {

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {


        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        String queryString = "";

        //get 방식인 경우
        if (request.getMethod().equals(HttpMethod.GET.getValue())) {
            String[] tokens = request.getUrl().split("\\?");
            queryString = tokens[1];
        } else if (request.getMethod().equals(HttpMethod.POST.getValue())) {
            queryString = request.getBody();
        }

        Map<String, String> queryParameter = HttpRequestUtils.parseQueryParameter(queryString);

        User user = new User(
                queryParameter.get("username"),
                queryParameter.get("password"),
                queryParameter.get("email"),
                queryParameter.get("phone")
        );
        memoryUserRepository.addUser(user);

        response.redirect("/index.html", null);

    }

}
