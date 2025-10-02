package webserver;

import db.MemoryUserRepository;
import db.Repository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    Repository repository = MemoryUserRepository.getInstance();

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String request = br.readLine();
            if (request == null || request.isEmpty()) {
                return;
            }
            //request :HTTPMethod Request-URL HTTPVersion
            String path = extractPath(request);
            if (path.equals("/")) {
                path = "/index.html";
            }

            int contentLength = 0;
            while(!request.equals("")){
                if (request.contains("Content-Length")) {
                    contentLength = getContentLength(request);
                }
                request = br.readLine();
            }

            /**
             * else if (path.startsWith("/user/signup")) {
              // GET 방식 회원가입
                int index = path.indexOf("?");
                String queryString = path.substring(index + 1);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.log(Level.INFO, user.getName());
                path = "/index.html";
            }*/

            if (path.equals("/user/signup")) {
                //POST방식 회원가입
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                repository.addUser(user);
                log.log(Level.INFO, user.getName());
                response302Heder(dos, "/index.html");

            } else if (path.equals("/user/login")) {
                //POST방식 로그인
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                User loginUser = repository.findUserById(params.get("userId"));
                System.out.println(loginUser);

                if (loginUser == null) {
                    responseResource(dos, "/user/login_failed.html");
                }
                if (loginUser.getPassword().equals(params.get("password"))) {
                    response302LoginSuccessHeader(dos);
                } else {
                    responseResource(dos, "/user/login_failed.html");
                }
            } else {
                responseResource(dos, path);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseResource(DataOutputStream dos, String path) throws IOException {
        byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private int getContentLength(String request) {
        String[] headerTokens = request.split(": ");
        return Integer.parseInt(headerTokens[1].trim());
    }

    private String extractPath(String request) {
        String[] part = request.split(" ");
        if (part.length >= 2) {
            return part[1];
        }
        return "/index.html";
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Heder(DataOutputStream dos, String request) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location:" + request + " \r\n");
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

}