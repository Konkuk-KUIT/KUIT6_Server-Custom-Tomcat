package controller;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;

public interface Controller {

    void execute(HttpRequest httpRequest, HttpResponse httpResponse);

    void setMemoryUserRepository(MemoryUserRepository memoryUserRepository);
}
