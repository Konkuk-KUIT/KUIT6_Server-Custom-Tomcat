package webserver.controller;

import constant.HttpContentType;
import constant.HttpStatus;
import constant.Url;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserListController implements Controller {
    @Override
    public void service(DataOutputStream dos, HttpRequest request, HttpResponse response) throws Exception {
        boolean isLogin = request.isLogin();
        if(isLogin){
            byte[] bodyFile = Files.readAllBytes(Paths.get(Url.USER_LIST.filePath()));
            HttpResponse.writeResponse(dos, HttpStatus.OK, HttpContentType.HTML.value(), bodyFile);
            System.out.println("아니 성공은 했딴다");
        }else{
            HttpResponse.writeRedirect(dos,Url.USER_LOGIN_FAILED.path());
            System.out.println("넌 실패했어");
        }
    }
}