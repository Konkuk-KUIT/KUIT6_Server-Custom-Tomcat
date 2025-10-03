package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;
import enums.HttpHeader;
import enums.HttpMethod;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

//public class RequestHandler implements Runnable {
//    Socket connection;
//    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
//
//    private final Repository repository;
//    private Controller controller = new ForwardController();
//
//
//    public RequestHandler(Socket connection) {
//        this.connection = connection;
//        repository = MemoryUserRepository.getInstance();
//    }
//
//    @Override
//    public void run() {
//        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
//        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
//            BufferedReader br = new BufferedReader(new InputStreamReader(in));
//            DataOutputStream dos = new DataOutputStream(out);
//
//            // Header 분석
//            HttpRequest httpRequest = HttpRequest.from(br);
//            HttpResponse httpResponse = new HttpResponse(dos);
//
//            // 요구 사항 1번
//            if (httpRequest.getMethod().equals(HttpMethod.GET.getValue()) && httpRequest.getUrl().endsWith(".html")) {
//                controller = new ForwardController();
//            }
//
//            if (httpRequest.getUrl().equals("/")) {
//                controller = new HomeController();
//            }
//
//            // 요구 사항 2,3,4번
//            if (httpRequest.getUrl().equals("/user/signup")) {
//                controller = new SignUpController();
//            }
//
//            // 요구 사항 5번
//            if (httpRequest.getUrl().equals("/user/login")) {
//                controller = new LoginController();
//            }
//
//            // 요구 사항 6번
//            if (httpRequest.getUrl().equals("/user/userList")) {
//                controller = new ListController();
//            }
//            controller.execute(httpRequest, httpResponse);
//
//        } catch (Exception e) {
//            log.log(Level.SEVERE, e.getMessage());
//            System.out.println(Arrays.toString(e.getStackTrace()));
//        }
//    }
//}

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
            HttpResponse httpResponse = new HttpResponse(dos);

            RequestMapper requestMapper = new RequestMapper(httpRequest, httpResponse);
            requestMapper.proceed();

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}