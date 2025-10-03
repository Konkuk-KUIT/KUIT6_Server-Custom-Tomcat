package main.java.http.controller;

import main.java.http.HttpRequest;
import main.java.http.HttpResponse;

import java.io.IOException;

public interface Controller {
    void execute(HttpRequest request, HttpResponse response) throws IOException;
}
