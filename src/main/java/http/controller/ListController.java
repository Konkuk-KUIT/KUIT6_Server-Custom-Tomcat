package http.controller;

import http.HttpRequest;
import http.HttpResponse;
import webserver.URL;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ListController implements Controller{
    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String logined = "";
        int i = 0;
        List<String> lines = request.getHeader().getLines();
        while (true) {
            if( i== lines.size()) {
                break;
            }
            final String line = lines.get(i++);
            if (line.isEmpty()) {
                break;
            }
            if (line.startsWith("Cookie: ")) {
                String cookieHeader = line.substring("Cookie:".length()).trim();
                String[] pairs = cookieHeader.split(";");
                for (String pair : pairs) {
                    String[] kv = pair.trim().split("=", 2); // 최대 2개만 split
                    if (kv.length == 2) {
                        String name = kv[0].trim();
                        String value = kv[1].trim();
                        if (name.equals("logined")) {
                            logined = value;
                        }
                    }
                }
            }
        }
        if(logined.equals("true")){
            response.forward( "/user/list.html");
        }
    }
}
