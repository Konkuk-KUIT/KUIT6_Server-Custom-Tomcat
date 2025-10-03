package webserver;

import controller.Controller;
import controller.SignInController;
import controller.SignUpController;
import controller.UserListController;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private Controller controller = new SignUpController();

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = HttpResponse.from(dos);

            // 페이지 별 라우팅
            String filePath = httpRequest.getFilePath();
            byte[] body = Files.readAllBytes(Paths.get(filePath));

            // 회원가입 시도
            if(httpRequest.isPost() && httpRequest.isSameUrl("/user/signup")) {
                controller = new SignUpController();
                controller.execute(httpRequest, httpResponse);
            }

            // 로그인 시도
            if(httpRequest.isPost() && httpRequest.isSameUrl("/user/login")){
                controller = new SignInController();
                controller.execute(httpRequest, httpResponse);
            }

            // 유저 리스트 출력
            if(httpRequest.isSameUrl("/user/userList")) {
                controller = new UserListController();
                controller.execute(httpRequest, httpResponse);
            }

            // css 확인
            if (httpRequest.isCss()) {
                httpResponse.response200Header(body.length, true);
            } else {
                httpResponse.response200Header(body.length);
            }

            httpResponse.responseBody(body);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}