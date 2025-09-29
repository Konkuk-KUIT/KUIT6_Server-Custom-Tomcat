package webserver;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;
import http.enums.HttpMethod;
import http.enums.RequestPath;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private Controller controller = new ForwardController();

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

            // Controller 선택 로직
            if (httpRequest.getPath().equals(RequestPath.ROOT.getValue())) {
                controller = new ForwardController();
            }

            if (httpRequest.getPath().equals(RequestPath.USER_SIGNUP.getValue()) && 
                httpRequest.getMethod() == HttpMethod.POST) {
                controller = new UserSignupController();
            }

            if (httpRequest.getPath().equals(RequestPath.USER_LOGIN.getValue()) && 
                httpRequest.getMethod() == HttpMethod.POST) {
                controller = new UserLoginController();
            }

            if (httpRequest.getPath().equals(RequestPath.USER_LIST.getValue())) {
                controller = new UserListController();
            }

            // Controller 실행
            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}