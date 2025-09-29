package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import http.enums.HttpMethod;
import http.enums.HttpHeader;
import http.enums.HttpStatus;
import http.enums.ContentType;
import http.enums.RequestPath;
import model.UserField;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpResponse httpResponse = new HttpResponse(out);

            // HttpRequest 객체로 HTTP 요청 파싱
            HttpRequest httpRequest = HttpRequest.from(br);
            log.log(Level.INFO, "Request Line: " + httpRequest.getMethod() + " " + httpRequest.getPath() + " " + httpRequest.getVersion());

            String method = httpRequest.getMethod().getValue();
            String path = httpRequest.getPath();
            String queryString = httpRequest.getQueryString();
            String cookieValue = httpRequest.getCookie();
            String requestBody = httpRequest.getBody();

            log.log(Level.INFO, "Method: " + method + ", Path: " + path + ", Query String: " + queryString);
            if (cookieValue != null) {
                log.log(Level.INFO, "Cookie received: " + cookieValue);
            }
            if (requestBody != null) {
                log.log(Level.INFO, "Request body: " + requestBody);
            }

            // 경로에 따른 파일 매핑 로직
            // 회원 가입 처리
            if (path.equals(RequestPath.USER_SIGNUP.getValue()) && (queryString != null || HttpMethod.POST.getValue().equals(method))) {
                // queryString parsing
                Map<String, String> params;

                if (HttpMethod.POST.getValue().equals(method)) {
                    // body에서 파라미터 추출
                    params = HttpRequestUtils.parseQueryParameter(requestBody);
                    log.log(Level.INFO, "POST Signup params: " + params);
                } else {
                    // GET에서 파라미터 추출
                    params = HttpRequestUtils.parseQueryParameter(queryString);
                    log.log(Level.INFO, "GET Signup params: " + params);
                }

                // User 객체 생성
                User newUser = new User(
                        params.get(UserField.USER_ID.getValue()),
                        params.get(UserField.PASSWORD.getValue()),
                        params.get(UserField.NAME.getValue()),
                        params.get(UserField.EMAIL.getValue())
                );

                // 메모리 저장소에 저장
                MemoryUserRepository repository = MemoryUserRepository.getInstance();
                repository.addUser(newUser);
                log.log(Level.INFO, "New User Registered: " + newUser.getUserId());

                // 302 리다이렉트로 메인 페이지로 이동
                httpResponse.redirect(RequestPath.INDEX.getValue());
                return;
            }

            // 로그인 처리
            if (path.equals(RequestPath.USER_LOGIN.getValue()) && HttpMethod.POST.getValue().equals(method)) {
                // POST 방식의 로그인만 처리
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(requestBody);
                log.log(Level.INFO, "Login params: " + params);

                String userId = params.get(UserField.USER_ID.getValue());
                String password = params.get(UserField.PASSWORD.getValue());

                // MemoryUserRepository에서 사용자 조회
                MemoryUserRepository repository = MemoryUserRepository.getInstance();
                User user = repository.findUserById(userId);

                // 인증 검증
                if (user != null && user.getPassword().equals(password)) {
                    // 로그인 성공: Cookie 설정 + 메인페이지로 리다이렉트
                    log.log(Level.INFO, "Login successful: " + userId);
                    httpResponse.redirectWithCookie(RequestPath.INDEX.getValue(), "logined=true");
                    return;
                } else {
                    // 로그인 실패: 에러페이지로 리다이렉트
                    log.log(Level.WARNING, "Login failed: " + userId);
                    httpResponse.redirect(RequestPath.USER_LOGIN_FAILED.getValue());
                    return;
                }
            }

            // userList 경로 처리
            if (path.equals(RequestPath.USER_LIST.getValue())) {
                // Cookie 에서 로그인 상태 확인
                if (cookieValue != null && cookieValue.contains("logined=true")) {
                    // user/list.html 파일
                    path = RequestPath.USER_LIST_HTML.getValue();
                } else {
                    // 비로그인 상태
                    httpResponse.redirect(RequestPath.USER_LOGIN_HTML.getValue());
                    return;
                }
            }

            // 1. 루트 경로 ("/") 처리 - 기본 페이지로 리다이렉트
            if (path.equals(RequestPath.ROOT.getValue())) {
                path = RequestPath.INDEX.getValue();
            }

            // 2. 보안 검증 - ../ 과 같은 디렉토리 traversal 공격 방지
            if (path.contains("..")) {
                log.log(Level.WARNING, "Path contains invalid path: " + path);
                // TODO: 에러 반환
                return;
            }

            try {
                // 3. 파일 forward
                httpResponse.forward(path);
                log.log(Level.INFO, "File forwarded successfully: " + path);

            } catch (IOException fileException) {
                // 4. 파일이 없거나 읽기 실패시 404 에러 응답
                log.log(Level.WARNING, "File not found or read error: " + path);
                httpResponse.notFound();
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}