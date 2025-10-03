package webserver;

import db.MemoryUserRepository;
import http.enums.HttpHeader;
import http.enums.HttpMethod;
import http.enums.HttpStatus;
import http.request.HttpRequest;
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

            HttpMethod method = httpRequest.getMethod();
            String path = httpRequest.getPath();
            Map<String, String> params = httpRequest.getParams();
            String cookie = httpRequest.getHeaders().get(HttpHeader.COOKIE.getValue());

            if (path.equals("/")) {
                path = "/index.html";
            }
            File file = new File("./webapp" + path);

            //int requestContentLength = Integer.parseInt(httpRequest.getHeaders().get(HttpHeader.CONTENT_LENGTH.getValue()));

            // [요구사항 6] 사용자 목록 출력
            if (path.startsWith("/user/userList")) {
                if (cookie != null && cookie.contains("logined=true")) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] body = fis.readAllBytes();
                        response200Header(dos, body.length, getContentType(path));
                        responseBody(dos, body);
                    }
                } else {
                    response302Header(dos, "/user/login.html");
                }
                return;
            }

            // [요구사항 5] 로그인 처리
            if (path.startsWith("/user/login") && method == HttpMethod.POST) {
                String userId = params.get(UserQueryKey.USER_ID.getKey());
                String password = params.get(UserQueryKey.PASSWORD.getKey());

                User user = MemoryUserRepository.getInstance().findUserById(userId);
                if (user != null && password.equals(user.getPassword())) {
                    response302LoginSuccessHeader(dos, "/index.html");
                } else {
                    response302LoginFailHeader(dos, "/user/login_failed.html");
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
                response302LoginSuccessHeader(dos, "/index.html");
                return;
            }


            // [요구사항 1 + 7] 정적 파일 요청일 경우
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] body = fis.readAllBytes();
                    response200Header(dos, body.length, getContentType(path));
                    responseBody(dos, body);
                }
            } else {
                byte[] body = "<h1>404 Not Found</h1>".getBytes();
                response404Header(dos, body.length);
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html;charset=utf-8";
        if (path.endsWith(".css")) return "text/css;charset=utf-8";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        return "application/octet-stream"; // 알 수 없는 경우 바이너리로 처리
    }

    // [200 OK]
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.OK.getCode() + " " + HttpStatus.OK.getMessage() + " \r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": " + contentType + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getValue() + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // [404 Not Found]
    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.NOT_FOUND.getCode() + " " + HttpStatus.NOT_FOUND.getMessage() + " \r\n");
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getValue() + ": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // [302 Found]
    private void response302Header(DataOutputStream dos, String redirectPath) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getMessage() + " \r\n");
            dos.writeBytes(HttpHeader.LOCATION.getValue() + ": " + redirectPath + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302LoginSuccessHeader(DataOutputStream dos, String redirectPath) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getMessage() + " \r\n");
            dos.writeBytes(HttpHeader.LOCATION.getValue() + ": " + redirectPath + "\r\n");
            dos.writeBytes(HttpHeader.SET_COOKIE.getValue() + ": logined=true\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302LoginFailHeader(DataOutputStream dos, String redirectPath) {
        try {
            dos.writeBytes("HTTP/1.1 " + HttpStatus.FOUND.getCode() + " " + HttpStatus.FOUND.getMessage() + " \r\n");
            dos.writeBytes(HttpHeader.LOCATION.getValue() + ": " + redirectPath + "\r\n");
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