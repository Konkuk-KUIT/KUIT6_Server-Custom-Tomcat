package webserver;

import db.MemoryUserRepository;
import http.HttpRequest;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest request = new HttpRequest(in);

            String url = request.getPath();

            if (request.getMethod().equals("POST") && url.equals("/user/signup")) {
                User user = new User(
                        request.getParameter("userId"),
                        request.getParameter("password"),
                        request.getParameter("name"),
                        request.getParameter("email"));
                MemoryUserRepository.getInstance().addUser(user);
                response302Header(dos, "/index.html");
                return;
            }

            if (request.getMethod().equals("POST") && url.equals("/user/login")) {
                User user = MemoryUserRepository.getInstance().findUserById(request.getParameter("userId"));

                if (user != null && user.getPassword().equals(request.getParameter("password"))) {
                    response302HeaderWithCookie(dos, "/index.html");
                } else {
                    response302Header(dos, "/user/login_failed.html");
                }
                return;
            }


            String path = url.split("\\?")[0];
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("./webapp" + path);
            if (!file.exists()) {
                response404(dos);
                return;
            }

            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            byte[] body = Files.readAllBytes(file.toPath());
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404(DataOutputStream dos) {
        try {
            String notFoundBody = "<h1>404 Not Found</h1>";
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + notFoundBody.getBytes().length + "\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(notFoundBody);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
