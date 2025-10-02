package webserver;

import db.MemoryUserRepository;
import enums.*;
import http.*;
import model.User;
import java.io.*;
import java.net.Socket;
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
            HttpResponse response = new HttpResponse(out);

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
                response.sendRedirect(RequestPath.INDEX.getPath());
                return;
            }

            // 회원가입 요청 - POST일때
            if (method == POST && RequestPath.SIGNUP.matches(path)) {
                Map<String, String> params = body.getFormData();
                saveUser(params);
                response.sendRedirect(RequestPath.INDEX.getPath());
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
                    response.sendRedirectWithCookie(RequestPath.INDEX.getPath(), "logined=true; Path=/");
                } else
                    response.sendRedirect(RequestPath.USER_LOGIN_FAILED.getPath());
                return;
            }

            // 사용자 목록 출력
            if (RequestPath.USER_LIST.matches(path)) {
                if (header.isLogined()) { // 로그인 되어있다면 userList.html 반환
                    response.forward(RequestPath.USER_LIST.getPath());
                } else {
                    response.sendRedirect(RequestPath.LOGIN.getPath() + ".html");
                }
                return;
            }

            //정적 파일 응답
            try {
               response.forward(path);
            } catch (IOException e) {
                response.sendNotFound();
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
}