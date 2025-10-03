package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;
import enumclasses.RedirectTarget;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import static enumclasses.RedirectTarget.LOGIN_FAILED;
import static enumclasses.URL.*;
import static enumclasses.URL.LOGIN;
import static enumclasses.URL.SIGNUP;
import static enumclasses.URL.USERLIST;
import static enumclasses.UserFactor.*;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private Controller controller = new ForwordController();

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    Repository repository = MemoryUserRepository.getInstance();

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