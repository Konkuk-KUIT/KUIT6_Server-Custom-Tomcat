package webserver;

import controller.Controller;
import controller.RequestMapping;
import http.util.HttpRequest;
import http.util.HttpResponse;
import http.util.httpMethod;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            HttpRequest request = HttpRequest.from(br);
            HttpResponse response = new HttpResponse(out);

            // POST 요청은 특별히 매핑된 컨트롤러를 찾습니다.
            if (request.getMethod() == httpMethod.POST) {
                Controller controller = RequestMapping.getController(request.getPath());
                controller.execute(request, response);
                return;
            }

            // 그 외 GET 요청 등은 URL에 맞는 컨트롤러를 찾아서 위임합니다.
            Controller controller = RequestMapping.getController(request.getPath());
            controller.execute(request, response);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}