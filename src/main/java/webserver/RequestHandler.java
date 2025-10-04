package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;
import enums.HtmlUrls;
import enums.HttpUrls;
import enums.UserKey;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static db.MemoryUserRepository.getInstance;
import static http.util.HttpRequestUtils.parseQueryParameter;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;
    private Controller controller = new ForwardController();


    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }


    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            // 요구 사항 1번
            if ("GET".equals(httpRequest.getMethod()) && httpRequest.getPath().endsWith(".html")) {
                controller = new ForwardController();
            }

            if (httpRequest.getPath().equals("/")) {
                controller = new HomeController();
            }

            // 요구 사항 2,3,4번
            if (httpRequest.getPath().equals(HttpUrls.SIGNUP.getPath())) {
                controller = new SignUpController();
            }

            // 요구 사항 5번
            if (httpRequest.getPath().equals(HttpUrls.LOGIN.getPath())) {
                controller = new LoginController();
            }

            // 요구 사항 6번
            if (httpRequest.getPath().equals(HttpUrls.USERLIST.getPath())) {
                controller = new ListController();
            }

            controller.execute(httpRequest, httpResponse);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}