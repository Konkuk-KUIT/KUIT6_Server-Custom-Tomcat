package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

public class ListController implements Controller{
    private BufferedReader br;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    MemoryUserRepository memoryUserRepository;

    public ListController(BufferedReader br) {
        this.br = br;
    }

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            String cookieHeader = null;
            String url = httpRequest.getUrl();
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("Cookie:")) {
                    cookieHeader = line.substring("Cookie:".length()).trim();
                }
            }
            if (cookieHeader != null && cookieHeader.contains("logined=true")) {
                memoryUserRepository.findAll().forEach(user -> log.info("USER:" + user.toString()));

                httpResponse.redirect("/user/list.html", null, url);
                return;
            }
            httpResponse.redirect("/index.html", null, url);
        } catch (IOException e) {
            log.log(java.util.logging.Level.SEVERE,e.getMessage());
        }
    }

    public void setMemoryUserRepository(MemoryUserRepository memoryUserRepository) {
        this.memoryUserRepository = memoryUserRepository;
    }
}
