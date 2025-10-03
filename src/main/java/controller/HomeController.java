package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;

public class HomeController implements Controller{
    MemoryUserRepository memoryUserRepository;

    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        System.out.println("HomeController");
        String url = "/index.html";
        String mimeTypes = httpRequest.getMimeType();
        httpResponse.forward(mimeTypes, url);
    }

    public void setMemoryUserRepository(MemoryUserRepository memoryUserRepository) {
        this.memoryUserRepository = memoryUserRepository;
    }



}
