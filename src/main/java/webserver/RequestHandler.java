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
            if (url == null){
                url = "/";
            }
            int q = url.indexOf('?');
            if (q>=0) {
                url = url.substring(0, q);
            }

            if (url.isBlank() || "/".equals(url) || "/index.html".equals(url)) {
                response.forward("index.html");
                return;
            }

            if(url.endsWith(".html")) {
                String resource = url.startsWith("/") ? url.substring(1) : url;
                // 앞에 / 가 붙으면 제거해주는 역할
                response.forward(resource);
            }

            Controller controller = requestMapper.findController(url);
            if (controller == null) {
                String resource = url.startsWith("/") ? url.substring(1) : url;
                if (resource.endsWith(".css") || resource.endsWith(".js") || resource.endsWith(".png") || resource.endsWith(".jpg") || resource.endsWith(".gif") || resource.endsWith(".ico")) {
                    response.forward(resource);
                } else {
                    response.send404(resource);
                }
            } else {
                controller.execute(request, response);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
