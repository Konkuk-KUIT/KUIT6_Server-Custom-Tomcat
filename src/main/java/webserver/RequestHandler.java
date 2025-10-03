package webserver;

import db.MemoryUserRepository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            String reqHeaderFirstLine = br.readLine();
            String[] tokens = reqHeaderFirstLine.split(" ");

            // 회원가입 시도
            if(tokens[0].equals("POST") && tokens[1].equals("/user/signup")) {
                String line;
                int contentLength = 0;
                while(!(line = br.readLine()).isEmpty()) {
                    if(line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }
                char[] bodyData = new char[contentLength];
                br.read(bodyData, 0, contentLength);
                String reqBody = new String(bodyData);
                String[] queryToken = reqBody.split("&");
                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
                memoryUserRepository.addUser(new User(queryToken[0].split("=")[1],
                        queryToken[1].split("=")[1],
                        queryToken[2].split("=")[1],
                        queryToken[3].split("=")[1]));
                response302Header(dos, "/");
            }

            // 로그인 시도
            if(tokens[0].equals("POST") && tokens[1].equals("/user/login")){
                String line;
                int contentLength = 0;
                while(!(line = br.readLine()).isEmpty()) {
                    if(line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }
                char[] bodyData = new char[contentLength];
                br.read(bodyData, 0, contentLength);
                String reqBody = new String(bodyData);
                String[] queryToken = reqBody.split("&");
                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
                String userId = queryToken[0].split("=")[1];
                String passwd = queryToken[1].split("=")[1];
                User user = memoryUserRepository.findUserById(userId);
                if(user!= null && user.getPassword().equals(passwd)) {
                    response302Header(dos, "/", true);
                } else {
                    response302Header(dos, "/user/login_failed.html");
                }

            }

            // 유저 리스트 출력
            if(tokens[1].equals("/user/userList")) {
                String line;
                boolean isLogined = false;
                while(!(line = br.readLine()).isEmpty()) {
                    if(line.startsWith("Cookie:")) {
                        String cookieValue = line.split(":")[1].trim();
                        isLogined = Boolean.parseBoolean(cookieValue.split("=")[1]);
                        break;
                    }
                }
                if(!isLogined) {
                    response302Header(dos, "/user/login.html");
                }
            }

            // 페이지 별 라우팅
            String filePath = switch (tokens[1]) {
                case "/qna/form.html" -> "webapp/qna/form.html";
                case "/qna/show.html" -> "webapp/qna/show.html";
                case "/user/form.html" -> "webapp/user/form.html";
                case "/user/userList" -> "webapp/user/list.html";
                case "/user/login.html" -> "webapp/user/login.html";
                case "/user/login_failed.html" -> "webapp/user/login_failed.html";
                case "/css/styles.css" -> "webapp/css/styles.css";
                default -> "webapp/index.html";
            };

            byte[] body = Files.readAllBytes(Paths.get(filePath));
            if(tokens[1].endsWith(".css")) {
                response200Header(dos, body.length, true);
            } else {
                response200Header(dos, body.length);
            }
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        response200Header(dos, lengthOfBodyContent, false);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, boolean isCss) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            if(isCss) {
                dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            } else {
                dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            }
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String route) {
        response302Header(dos, route, false);
    }
    private void response302Header(DataOutputStream dos, String route, boolean hasCookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + route + " \r\n");
            if(hasCookie) {
                dos.writeBytes("Set-Cookie: logined=true \r\n");
            }
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