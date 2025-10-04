package webserver.controller;

import constant.HttpContentType;
import constant.HttpStatus;
import constant.Url;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserLoginFailController implements Controller {
    @Override
    public void service(DataOutputStream dos, HttpRequest request, HttpResponse response) throws Exception {
        byte[] bodyFile = Files.readAllBytes(Paths.get(Url.USER_LOGIN_FAILED.filePath()));
        HttpResponse.writeResponse(dos, HttpStatus.OK, HttpContentType.HTML.value(), bodyFile);

    }
    }
