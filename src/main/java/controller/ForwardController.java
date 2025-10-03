package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;

public class ForwardController implements Controller{
    private MemoryUserRepository memoryUserRepository;
    @Override
    public void execute(HttpRequest httpRequest, HttpResponse httpResponse) {
        String url = httpRequest.getUrl();
        String mimeTypes = httpRequest.getMimeType();
        httpResponse.forward(mimeTypes, url);
    }

    public void setMemoryUserRepository(MemoryUserRepository memoryUserRepository) {
        this.memoryUserRepository = memoryUserRepository;
    }
}
