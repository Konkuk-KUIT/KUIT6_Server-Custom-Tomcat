package webserver;

import constant.HttpContentType;
import constant.HttpStatus;
import constant.Url;
import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;
import webserver.controller.Controller;
import webserver.controller.ControllerFactory;
import webserver.controller.StaticFileController;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }


    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse();
            DataOutputStream dos = new DataOutputStream(out);

            String path = request.getPath();
            Controller controller = ControllerFactory.getController(path);

            if (controller == null) {
                // css, js 등 정적 파일 처리용
                controller = new StaticFileController();
            }

            controller.service(dos, request, response);


        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
