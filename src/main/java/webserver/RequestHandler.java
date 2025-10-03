package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;

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
            HttpResponse response = new HttpResponse(out);
            DataOutputStream dos = new DataOutputStream(out);
            MemoryUserRepository userDB = MemoryUserRepository.getInstance();
            String path = request.getPath();
            String method = request.getMethod();
            int contentLength = request.getContentLength();
            boolean isLogin = request.isLogin();
            String body = request.getBody();



            //여기가 지금
            if (path.equals("/") || path.equals("/index.html")) {
                // index.html 반환

                byte[] bodyFile = Files.readAllBytes(Paths.get("./webapp/index.html"));
                response.response200Header(bodyFile);
                response.responseBody(bodyFile);
            }
            else if (path.endsWith(".css")) {
                // ✅ CSS 파일 처리
                byte[] bodyFile = Files.readAllBytes(Paths.get("./webapp" + path));
                response.responseCssHeader( bodyFile);
                response.responseBody( bodyFile);
            }
            else if (path.equals("/user/form.html")) {
                // 회원가입 폼 반환
                byte[] bodyFile = Files.readAllBytes(Paths.get("./webapp/user/form.html"));
                response.response200Header( bodyFile);
                response.responseBody(bodyFile);
            }
            else if (path.startsWith("/user/signup")) {

                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                String userId = params.get("userId");
                String password = params.get("password");
                String name = params.get("name");
                String email = params.get("email");
                userDB.addUser(new User(userId, password, name, email));
                User existingUser = userDB.findUserById(userId);
                response.response302Header( "/index.html");
            }
            else if (path.equals("/user/login.html")) {
                byte[] bodyFile = Files.readAllBytes(Paths.get("./webapp/user/login.html"));
                response.response200Header( bodyFile);
                response.responseBody( bodyFile);
            }
            else if(path.equals("/user/login_failed.html")){
                byte[] bodyFile = Files.readAllBytes(Paths.get("./webapp/user/login_failed.html"));
                response.response200Header( bodyFile);
                response.responseBody(bodyFile);
            }

            else if (path.equals("/user/login")) {
                if (method.equals("POST")) {
                    Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

                    String userId = params.get("userId");
                    String password = params.get("password");

                    User existingUser = userDB.findUserById(userId);
                    // 로그인 검증
                    if (existingUser != null) {
                        // 로그인 성공
                        response.response302HeaderWithCookie("/index.html"); // 홈으로 리다이렉트
                    } else {
                        // 로그인 실패
                        response.response302Header("/user/login_failed.html");
                    }
                }
            }else if (path.equals("/user/userList")) {
                // 자 여긴 만약에 했을때임
                if(isLogin){
                    byte[] bodyFile = Files.readAllBytes(Paths.get("./webapp/user/list.html"));
                    response.response200Header(bodyFile);
                    response.responseBody(bodyFile);
                    System.out.println("아니 성공은 했딴다");
                }else{
                    response.response302Header("/user/login.html");
                    System.out.println("넌 실패했어");
                }

            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }



}
