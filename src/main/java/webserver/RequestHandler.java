package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.UserSignupController;
import controller.UserLoginController;
import controller.UserListController;
import http.HttpRequest;
import http.HttpResponse;
import http.enums.HttpMethod;
import http.enums.RequestPath;

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
            if (path.equals(RequestPath.USER_SIGNUP.getValue()) && HttpMethod.POST.getValue().equals(method)) {
                UserSignupController controller = new UserSignupController();
                controller.execute(httpRequest, httpResponse);
                return;
            }

            // 로그인 처리 (POST 방식만)
            if (path.equals(RequestPath.USER_LOGIN.getValue()) && HttpMethod.POST.getValue().equals(method)) {
                UserLoginController controller = new UserLoginController();
                controller.execute(httpRequest, httpResponse);
                return;
            }

            // userList 경로 처리
            if (path.equals(RequestPath.USER_LIST.getValue())) {
                UserListController controller = new UserListController();
                controller.execute(httpRequest, httpResponse);
                return;
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