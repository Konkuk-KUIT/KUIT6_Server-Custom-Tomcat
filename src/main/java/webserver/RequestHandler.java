package webserver;

import db.MemoryUserRepository;
import http.enums.HttpHeader;
import http.enums.HttpMethod;
import http.enums.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;
import model.UserQueryKey;

import java.io.*;
import java.net.Socket;
import java.util.Map;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            HttpMethod method = httpRequest.getMethod();
            String path = httpRequest.getPath();
            Map<String, String> params = httpRequest.getParams();
            String cookie = httpRequest.getHeaders().get(HttpHeader.COOKIE.getValue());

            // [요구사항 6] 사용자 목록 출력
            if (path.startsWith("/user/userList")) {
                if (cookie != null && cookie.contains("logined=true")) {
                    httpResponse.forward("/user/list.html");
                } else {
                    httpResponse.redirect("/user/login.html");
                }
                return;
            }

            // [요구사항 5] 로그인 처리
            if (path.startsWith("/user/login") && method == HttpMethod.POST) {
                String userId = params.get(UserQueryKey.USER_ID.getKey());
                String password = params.get(UserQueryKey.PASSWORD.getKey());

                User user = MemoryUserRepository.getInstance().findUserById(userId);
                if (user != null && password.equals(user.getPassword())) {
                    //response302LoginSuccessHeader(dos, "/index.html");
                    httpResponse.redirectSuccessLogin("/index.html");
                } else {
                    httpResponse.redirect("/user/login_failed.html");
                }
                return;
            }

            // 회원가입 처리
            if (path.startsWith("/user/signup")) {
                User user = new User(
                        params.get(UserQueryKey.USER_ID.getKey()),
                        params.get(UserQueryKey.PASSWORD.getKey()),
                        params.get(UserQueryKey.NAME.getKey()),
                        params.get(UserQueryKey.EMAIL.getKey())
                );

                MemoryUserRepository.getInstance().addUser(user);
                httpResponse.redirect("/index.html");
                return;
            }


            // [요구사항 1 + 7] 정적 파일 요청일 경우
            if (path.equals("/")) {
                httpResponse.forward("/index.html");
            } else {
                httpResponse.forward(path);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}