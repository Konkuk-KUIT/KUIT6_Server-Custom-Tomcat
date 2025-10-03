package webserver;

import constant.HttpContentType;
import constant.HttpStatus;
import constant.Url;
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
            DataOutputStream dos = new DataOutputStream(out);
            MemoryUserRepository userDB = MemoryUserRepository.getInstance();
            String path = request.getPath();
            String method = request.getMethod();
            int contentLength = request.getContentLength();
            boolean isLogin = request.isLogin();
            String body = request.getBody();




            //여기가 지금
            if (path.equals(Url.ROOT.path()) || path.equals(Url.INDEX.path())) {
                // index.html 반환

                byte[] bodyFile = Files.readAllBytes(Paths.get(Url.ROOT.filePath()));
                HttpResponse.writeResponse(dos, HttpStatus.OK,HttpContentType.HTML.value(), bodyFile);
            }
            else if (path.endsWith(".css")) {
                byte[] bodyFile = Files.readAllBytes(Paths.get(Url.CSS.filePath() + path));
                HttpResponse.writeResponse(dos, HttpStatus.OK, HttpContentType.CSS.value(), bodyFile);

            }
            else if (path.equals(Url.USER_FORM.path())) {
                // 회원가입 폼 반환
                byte[] bodyFile = Files.readAllBytes(Paths.get(Url.USER_FORM.filePath()));
                HttpResponse.writeResponse(dos, HttpStatus.OK,HttpContentType.HTML.value(), bodyFile);

            }
            else if (path.startsWith(Url.USER_SIGNUP.path())) {

                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                String userId = params.get("userId");
                String password = params.get("password");
                String name = params.get("name");
                String email = params.get("email");
                userDB.addUser(new User(userId, password, name, email));
                User existingUser = userDB.findUserById(userId);
                HttpResponse.writeRedirect(dos,Url.INDEX.path());
            }
            else if (path.equals(Url.USER_LOGIN_HTML.path())) {
                byte[] bodyFile = Files.readAllBytes(Paths.get(Url.USER_LOGIN_HTML.filePath()));
                HttpResponse.writeResponse(dos, HttpStatus.OK, HttpContentType.HTML.value(), bodyFile);

            }
            else if(path.equals(Url.USER_LOGIN_FAILED.path())){
                byte[] bodyFile = Files.readAllBytes(Paths.get(Url.USER_LOGIN_FAILED.filePath()));
                HttpResponse.writeResponse(dos, HttpStatus.OK,HttpContentType.HTML.value(), bodyFile);

            }

            else if (path.equals(Url.USER_LOGIN.path())) {
                if (method.equals("POST")) {
                    Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

                    String userId = params.get("userId");
                    String password = params.get("password");

                    User existingUser = userDB.findUserById(userId);
                    // 로그인 검증
                    if (existingUser != null) {
                        // 로그인 성공
                        HttpResponse.writeRedirectWithCookie(dos,Url.INDEX.path(), "logined=true");
                    } else {
                        // 로그인 실패
                        HttpResponse.writeRedirect(dos,Url.USER_LOGIN_FAILED.path());
                    }
                }
            }else if (path.equals(Url.USER_LIST.path())) {
                // 자 여긴 만약에 했을때임
                if(isLogin){
                    byte[] bodyFile = Files.readAllBytes(Paths.get(Url.USER_LIST.filePath()));
                    HttpResponse.writeResponse(dos, HttpStatus.OK,HttpContentType.HTML.value(), bodyFile);
                    System.out.println("아니 성공은 했딴다");
                }else{
                    HttpResponse.writeRedirect(dos,Url.USER_LOGIN_FAILED.path());
                    System.out.println("넌 실패했어");
                }

            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }



}
