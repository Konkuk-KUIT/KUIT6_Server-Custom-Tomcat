package controller;

import http.util.HttpRequest;
import http.util.HttpResponse;
import http.util.UrlPath;

import java.io.BufferedReader;
import java.io.DataOutput;

public class ListController implements Controller {
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception{
        boolean logined = false;
        BufferedReader br = httpRequest.getBr();
        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.contains("logined=true")) {
                System.out.println(1);
                logined = true;
            }
        }
        if (logined) {
            httpResponse.response302Header(UrlPath.USER_LIST.getPath());
            return ;
        }
        httpResponse.response302Header(UrlPath.LOGIN_PAGE.getFilePath());
    }
}
