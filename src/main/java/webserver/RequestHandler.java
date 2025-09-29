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
import controller.StaticFileController;
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

            // 정적 파일 처리 (모든 나머지 요청)
            StaticFileController controller = new StaticFileController();
            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}