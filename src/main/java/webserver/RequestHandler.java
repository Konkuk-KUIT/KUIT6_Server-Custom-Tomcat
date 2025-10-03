package webserver;

import controller.*;

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
            HttpRequest httpRequest = HttpRequest.from(in);
            HttpResponse httpResponse = HttpResponse.from(out);

            /*
             * else if (path.startsWith("/user/signup")) {
              // GET 방식 회원가입
                int index = path.indexOf("?");
                String queryString = path.substring(index + 1);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.log(Level.INFO, user.getName());
                path = "/index.html";
            }*/
            RequestMapper requestMapper = new RequestMapper(httpRequest, httpResponse);
            requestMapper.proceed();

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}