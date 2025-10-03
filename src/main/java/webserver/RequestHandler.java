package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.HttpRequest;
import http.HttpResponse;
import http.RequestMapper;
import http.controller.Controller;
import http.controller.ForwardController;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;
    private Controller controller = new ForwardController();


    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            RequestMapper requestMapper = new RequestMapper(httpRequest, httpResponse);
            requestMapper.proceed();

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}
