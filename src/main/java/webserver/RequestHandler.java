package webserver;

import controller.Controller;
import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private final Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final RequestMapper requestMapper = new RequestMapper();

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = HttpRequest.from(in);
            HttpResponse response = new HttpResponse(out);

            String url = request.getPath();
            Controller controller = requestMapper.findController(url);
            controller.execute(request, response);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}