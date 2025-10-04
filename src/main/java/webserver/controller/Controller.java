package webserver.controller;


import webserver.HttpRequest;
import webserver.HttpResponse;

import java.io.DataOutputStream;

public interface Controller {
    void service(DataOutputStream dos, HttpRequest request, HttpResponse response) throws Exception;
}