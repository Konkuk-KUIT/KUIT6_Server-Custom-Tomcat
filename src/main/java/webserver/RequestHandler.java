package webserver;

import webserver.controller.*;
import webserver.enums.HttpMethod;
import webserver.enums.Path;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // Header 분석
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            // 요구 사항 1번
            if (httpRequest.getMethod() == HttpMethod.GET && httpRequest.getPath().endsWith(".html")) {
                controller = new ForwardController();
            }

            if (httpRequest.getPath().equals(Path.HOME.getValue())) {
                controller = new HomeController();
            }

            // 요구 사항 2,3,4번
            if (httpRequest.getPath().equals(Path.SIGNUP.getValue())) {
                controller = new SignUpController();
            }

            // 요구 사항 5번
            if (httpRequest.getPath().equals(Path.LOGIN.getValue())) {
                controller = new LoginController();
            }

            // 요구 사항 6번
            if (httpRequest.getPath().equals(Path.USER_LIST.getValue())) {
                controller = new ListController();
            }
            controller.execute(httpRequest, httpResponse);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }




}