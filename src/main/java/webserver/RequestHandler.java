package webserver;

import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.constants.HttpHeader;
import http.constants.HttpStatusCode;
import http.util.IOUtils;
import http.util.MimeTypes;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = HttpResponse.from(dos);
            String url = httpRequest.getUrl();
            String mimeTypes = httpRequest.getMimeType();


            if (url.equals("/")) {
                url = "/index.html";
                httpResponse.forward(mimeTypes, url);
                return;
            }

            if(url.equals("/user/signup")) {
                String body = httpRequest.getHttpBody();
                String[] queryParamArr = body.split("&");
                String userId = queryParamArr[0].split("=")[1];
                String userPw = queryParamArr[1].split("=")[1];
                String userName = queryParamArr[2].split("=")[1];
                String userEmail = queryParamArr[3].split("=")[1];
                User user = new User(userId, userPw, userName, userEmail);
                memoryUserRepository.addUser(user);
                httpResponse.redirect("/index.html", null, url);
                return;
            }

            if(url.equals("/user/login")) {

                String body = httpRequest.getHttpBody();
                String[] queryParamArr = body.split("&");
                String userId = queryParamArr[0].split("=")[1];
                String userPw = queryParamArr[1].split("=")[1];
                User user = memoryUserRepository.findUserById(userId);
                if(user != null && userPw.equals(user.getPassword())) {
                    httpResponse.redirect("/index.html", "logined=true; Path=/; HttpOnly", url);
                    return;
                }
                httpResponse.redirect("/user/login_failed.html", null, url);
                return;
            }

            if(url.equals("/user/userlist")) {
                String cookieHeader = null;
                String line;
                while ((line = br.readLine()) != null && !line.isEmpty()) {
                    if (line.startsWith("Cookie:")) {
                        cookieHeader = line.substring("Cookie:".length()).trim();
                    }
                }
                if(cookieHeader != null && cookieHeader.contains("logined=true")) {
                    memoryUserRepository.findAll().forEach(user -> log.info("USER:" + user.toString()));

                    httpResponse.redirect("/user/list.html", null, url);
                    return;
                }
                httpResponse.redirect("/index.html", null, url);
                return;
            }

            httpResponse.forward(mimeTypes, url);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}