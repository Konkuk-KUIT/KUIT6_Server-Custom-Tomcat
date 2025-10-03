package main.java.webserver;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.charset.StandardCharsets;

import main.java.db.MemoryUserRepository;
import main.java.http.HttpRequest;
import main.java.http.HttpResponse;
import main.java.http.enums.HttpMethod;
import main.java.http.enums.QueryKey;
import main.java.model.User;


public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

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

            final HttpMethod httpMethod = request.getMethod();
            final String path = request.getPath();


//            if (request.getMethod() == HttpMethod.GET && path.startsWith("/user/signup")) {
//                String[] parts = path.split("\\?", 2);
//                String query = parts.length > 1 ? parts[1] : "";
//
//                Map<String, String> params = parseQueryString(query); // (아래 유틸 참고)
//
//                User u = new User(
//                        urlDecode(params.get("userId")),
//                        urlDecode(params.get("password")),
//                        urlDecode(params.get("name")),
//                        urlDecode(params.get("email"))
//                );
//
//                MemoryUserRepository.getInstance().addUser(u);
//                response302Header(dos, "/index.html");
//                dos.flush();
//                return;
//            }


            if (httpMethod == HttpMethod.POST && "/user/signup".equals(path)) {
                User u = new User(
                        request.getParam(QueryKey.USER_ID.getKey()),
                        request.getParam(QueryKey.PASSWORD.getKey()),
                        request.getParam(QueryKey.NAME.getKey()),
                        request.getParam(QueryKey.EMAIL.getKey())
                );
                MemoryUserRepository.getInstance().addUser(u);
                response.redirect("/index.html");
                return;
            }


            if (httpMethod == HttpMethod.POST && "/user/login".equals(path)) {
                String userId = request.getParam(QueryKey.USER_ID.getKey());
                String password = request.getParam(QueryKey.PASSWORD.getKey());
                User found = MemoryUserRepository.getInstance().findUserById(userId);
                if (found != null && found.getPassword().equals(password)) {
                    response.redirectWithCookie("/index.html", "logined=true; Path=/");
                } else {
                    response.redirect("/user/login_failed.html");
                }
                return;
            }

            if (httpMethod == HttpMethod.GET && "/user/list.html".equals(path)) {
                String logined = request.getCookie("logined");
                if (!"true".equals(logined)) {
                    response.redirect("/index.html");
                    return;
                }
            }

            // 정적 파일 처리
            String resourcePath = "/".equals(path) ? "/index.html" : path;
            response.forward(resourcePath);

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