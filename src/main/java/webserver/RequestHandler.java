package webserver;

import db.MemoryUserRepository;
import db.Repository;
import enumclasses.RedirectTarget;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import static enumclasses.HttpHeader.*;
import static enumclasses.RedirectTarget.LOGIN_FAILED;
import static enumclasses.StatusCode.*;
import static enumclasses.URL.*;
import static enumclasses.URL.LOGIN;
import static enumclasses.URL.SIGNUP;
import static enumclasses.URL.USERLIST;
import static enumclasses.UserFactor.*;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(in);
            String path = httpRequest.getPath();

            /**
             * else if (path.startsWith("/user/signup")) {
              // GET 방식 회원가입
                int index = path.indexOf("?");
                String queryString = path.substring(index + 1);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.log(Level.INFO, user.getName());
                path = "/index.html";
            }*/

            if (path.equals(SIGNUP.URL)) {
                //POST방식 회원가입
                Map<String, String> params = httpRequest.getParam();
                User user = new User(params.get(USERID.key), params.get(PASSWORD.key), params.get(NAME.key), params.get(EMAIL.key));
                repository.addUser(user);
                log.log(Level.INFO, user.getName());
                response302Heder(dos, HOME.URL);

            } else if (path.equals(LOGIN.URL)) {
                Map<String, String> params = httpRequest.getParam();
                //POST방식 로그인
                User loginUser = repository.findUserById(params.get(USERID.key));

                System.out.println(loginUser);

                if (loginUser == null) {
                    responseResource(dos, LOGIN_FAILED.route);
                }else if (loginUser.getPassword().equals(params.get(PASSWORD.key))) {
                    System.out.println("응답했음");
                    response302LoginSuccessHeader(dos);
                } else {
                    responseResource(dos, LOGIN_FAILED.route);
                }
            } else if (path.equals(USERLIST.URL)) {
                //로그인 상태일 때 userList조회
                if (!httpRequest.isLogined()) {
                    response302Heder(dos, RedirectTarget.LOGIN.route);
                }
                responseResource(dos, RedirectTarget.USERLIST.route);
            }
            else if (httpRequest.getPath().endsWith(".css")) {
                response200CssHeader(dos, path);
            }
            else {
                responseResource(dos, path);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200CssHeader(DataOutputStream dos, String path) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
        try{
            dos.writeBytes(OK.line()+" \r\n");
            dos.writeBytes(CONTENT_TYPE.text+": text/css\r\n");
            dos.writeBytes(CONTENT_LENGTH.text+": " + body.length + "\r\n");
            dos.writeBytes("\r\n");
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }

        responseBody(dos, body);

    }

    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes(FOUND.line()+" \r\n");
            dos.writeBytes(SET_COOKIE.text+": logined=true \r\n");
            dos.writeBytes(LOCATION.text+": /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseResource(DataOutputStream dos, String path) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }


    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes(OK.line()+" \r\n");
            dos.writeBytes(CONTENT_TYPE.text+": text/html;charset=utf-8\r\n");
            dos.writeBytes(CONTENT_LENGTH.text +": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Heder(DataOutputStream dos, String request) {
        try {
            dos.writeBytes(FOUND.line()+" \r\n");
            dos.writeBytes(LOCATION.text+": " + request + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}