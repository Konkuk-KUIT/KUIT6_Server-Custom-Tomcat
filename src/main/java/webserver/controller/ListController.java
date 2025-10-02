package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.enums.HttpStatus;

public class ListController implements Controller {
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
        String logined = httpRequest.getCookie("logined");
        if (!Boolean.parseBoolean(logined)) { //로그인 cookie 없음
            httpResponse.setStatus(HttpStatus.FOUND_302);
            httpResponse.setHeader("Location", "/index.html");
            httpResponse.setBody();
        }
        else{ //로그인 cookie 존재
            byte[] body = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("./webapp/user/list.html"));

            httpResponse.setStatus(HttpStatus.OK_200);
            httpResponse.setHeader("Content-Type", "text/html; charset=utf-8");
            httpResponse.setHeader("Content-Length", String.valueOf(body.length));
            httpResponse.setBody(body);
        }
    }
}
