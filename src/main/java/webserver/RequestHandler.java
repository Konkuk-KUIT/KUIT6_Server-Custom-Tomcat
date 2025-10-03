package main.java.webserver;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets;

import main.java.http.HttpRequest;
import main.java.http.HttpResponse;
import main.java.http.controller.Controller;
import main.java.http.controller.Router;
import main.java.http.controller.Routes;


public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private static final Router router = Routes.create();

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            HttpRequest request = HttpRequest.from(br);
            HttpResponse response = new HttpResponse(out);

            Controller controller = router.resolve(request.getMethod(), request.getPath());

            controller.execute(request, response);


        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (Exception ignore) {
            }
        }

    }


}