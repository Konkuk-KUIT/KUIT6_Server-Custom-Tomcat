package webserver;

import db.MemoryUserRepository;
import enums.*;
import http.HttpBody;
import http.HttpHeaderMap;
import http.HttpRequest;
import http.HttpStartLine;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enums.HttpMethod.GET;
import static enums.HttpMethod.POST;


public class RequestHandler implements Runnable {
    Socket connection; // 소켓 - 데이터 주고받기 위한 양쪽 끝 단자
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

            // HttpRequest 파싱
            HttpRequest httpRequest = HttpRequest.from(br);

            HttpStartLine startLine = httpRequest.getStartLine();
            HttpHeaderMap header = httpRequest.getHeader();
            HttpBody body = httpRequest.getBody();

            HttpMethod method = startLine.getMethod(); // 요청 방식
            String path = startLine.getPath(); // 요청한 경로

            if (RequestPath.ROOT.matches(path)) {
                path = RequestPath.ROOT.getRedirect();
            }

            // 회원가입 요청 - GET일때
            if (method == GET && RequestPath.SIGNUP.matches(path)) {
                Map<String, String> params = startLine.getQueryParams();
                saveUser(params);
                // index.html로 리다이렉트
                response302Header(dos, RequestPath.INDEX.getPath());
                return;
            }

            // 회원가입 요청 - POST일때
            if (method == POST && RequestPath.SIGNUP.matches(path)) {
                Map<String, String> params = body.getFormData();
                saveUser(params);
                response302Header(dos, RequestPath.INDEX.getPath());
                return;
            }

            // 로그인 요청
            if (method == POST && RequestPath.LOGIN.matches(path)) {
                Map<String, String> params = body.getFormData();

                String userId = params.get(UserParam.USER_ID.getKey());
                String password = params.get(UserParam.PASSWORD.getKey());

                MemoryUserRepository repository = MemoryUserRepository.getInstance();
                User user = repository.findUserById(userId);

                if (user != null && user.getPassword().equals(password)) { // 로그인 성공했으면
                    response302LoginSuccess(dos, RequestPath.INDEX.getPath());
                } else
                    response302Header(dos, RequestPath.USER_LOGIN_FAILED.getPath());
                return;
            }

            // 사용자 목록 출력
            if (RequestPath.USER_LIST.matches(path)) {
                if (header.isLogined()) { // 로그인 되어있다면 userList.html 반환
                    String filePath = "webapp" + RequestPath.USER_LIST.getPath();
                    byte[] responseBody = Files.readAllBytes(Paths.get(filePath));
                    response200Header(dos, responseBody.length, filePath);
                    responseBody(dos, responseBody);
                } else {
                    response302Header(dos, RequestPath.LOGIN.getPath()+".html");
                }
                return;
            }

            String filePath = "webapp" + path; // URL 경로 → 프로젝트 폴더 경로로 매핑
            try {
                byte[] responseBody = Files.readAllBytes(Paths.get(filePath));
                response200Header(dos, responseBody.length, filePath); // 응답 헤더: 200 OK
                responseBody(dos, responseBody);             // 응답 바디: 파일 내용
            } catch (IOException e) {
                response404Header(dos);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }


    private static void saveUser(Map<String, String> params) {
        User user = new User(
                params.get(UserParam.USER_ID.getKey()),
                params.get(UserParam.PASSWORD.getKey()),
                params.get(UserParam.NAME.getKey()),
                params.get(UserParam.EMAIL.getKey())
        );

        // 파싱한 데이터로 User 객체 만들어준다.
        MemoryUserRepository.getInstance().addUser(user);
    }

    private void response404Header(DataOutputStream dos) {
        try {
            dos.writeBytes(HttpStatus.NOT_FOUND.toStatusLine());
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes("<h1>404 Not Found</h1>");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302LoginSuccess(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(HttpStatus.FOUND.toStatusLine());
            dos.writeBytes(HttpHeader.LOCATION.getValue() + ": " + path + "\r\n");
            dos.writeBytes(HttpHeader.SET_COOKIE.getValue() + ": logined=true\r\n"); // 로그인 여부 쿠키 저장
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(HttpStatus.FOUND.toStatusLine());            dos.writeBytes(HttpHeader.LOCATION.getValue() + ": " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String path) {
        try {
            dos.writeBytes(HttpStatus.OK.toStatusLine());
            if (path.endsWith(".css")) { // .css 요청이 오면 브라우저가 CSS로 인식해서 스타일 적용
                dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": text/css\r\n");
            } else {
                dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
            }
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getValue() + ": " + lengthOfBodyContent + "\r\n");
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