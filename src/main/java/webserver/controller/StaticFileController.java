package webserver.controller;

import constant.HttpContentType;
import constant.HttpStatus;
import constant.Url;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.DataOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StaticFileController implements Controller {
    @Override
    public void service(DataOutputStream dos, HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        byte[] bodyFile = Files.readAllBytes(Paths.get(Url.CSS.filePath() + path));
        HttpResponse.writeResponse(dos, HttpStatus.OK, HttpContentType.CSS.value(), bodyFile);
    }
}
