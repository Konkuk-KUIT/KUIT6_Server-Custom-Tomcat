package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            byte[] body = "hello world!".getBytes();

            //소켓에서 받은 inputStream 읽어서 파싱하기
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            String[] parts = requestLine.split(" ", 3);
            if (parts.length < 2) {
                return;
            }

            String requestMethod = parts[0];
            String requestURI = parts[1];
            System.out.println(requestMethod + " " + requestURI);


            // 요청 매핑 부분
            switch (requestURI) {
                case "/" -> {
                    response(dos, "webapp/index.html");
                    return;
                }
                case "/qna/show.html" -> {
                    response(dos, "webapp/qna/show.html");
                    return;
                }
                case "/qna/form.html" -> {
                    response(dos, "webapp/qna/form.html");
                    return;
                }
                case "/user/form.html" -> {
                    response(dos, "webapp/user/form.html");
                    return;
                }
                case "/user/userList" -> {
                    response(dos, "webapp/user/list.html");
                    return;
                }
                case "/user/login.html" -> {
                    response(dos, "webapp/user/login.html");
                    return;
                }
                case "/login.failed.html" -> {
                    response(dos, "webapp/login.failed.html");
                    return;
                }
            }

            if(requestMethod.equals("GET") && isSignUp(requestURI)){
                System.out.println(1);
                responseSignUp(requestURI);
                response302Header(dos, "/");
                return;
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void responseSignUp( String requestURI) {
        String query = requestURI.split("\\?",2)[1];
        Map<String, String> userInfoMap = HttpRequestUtils.parseQueryParameter(query);

        User user = User.factory(userInfoMap);

        saveUser(user);
    }

    private void saveUser(User user) {
        MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
        memoryUserRepository.addUser(user);

    }

    private boolean isSignUp(String requestURI) {
        return requestURI.contains("/user/signup?");
    }

    private void response(DataOutputStream dos, String filePath) {
        try {
            byte[] body = Files.readAllBytes(Paths.get(filePath));
            response200Header(dos, body.length);
            dos.write(body, 0, body.length);
            dos.flush();

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
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

    private void response302Header(DataOutputStream dos, String request) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + request + "\r\n");
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