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
            int contentLength = 0;
            MemoryUserRepository userDB = MemoryUserRepository.getInstance();


            String requestLine = br.readLine();
            String[] tokens = requestLine.split(" ");
            String method = tokens[0];

            if (method.equals("POST")) {
                while (true) {
                    String line = br.readLine();
                    if (line.equals("")) break; // ← 빈 줄 만나면 헤더 끝!

                    if (line.startsWith("Content-Length")) {
                        contentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }


            }


            String path = tokens[1];
            if (path.equals("/") || path.equals("/index.html")) {
                // index.html 반환

                byte[] body = Files.readAllBytes(Paths.get("./webapp/index.html"));
                response200Header(dos, body);
                responseBody(dos, body);
            }
            else if (path.equals("/user/form.html")) {
                // 회원가입 폼 반환
                byte[] body = Files.readAllBytes(Paths.get("./webapp/user/form.html"));
                response200Header(dos, body);
                responseBody(dos, body);
            }
            else if (path.startsWith("/user/signup")) {
                char[] bodyData = new char[contentLength];
                br.read(bodyData, 0, contentLength);
                String body = new String(bodyData);
                System.out.println("요청 본문: " + body);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                String userId = params.get("userId");
                String password = params.get("password");
                String name = params.get("name");
                String email = params.get("email");
                userDB.addUser(new User(userId, password, name, email));
                User existingUser = userDB.findUserById(userId);
                System.out.println(userId);
                System.out.println(password);
                System.out.println(existingUser);
                response302Header(dos, "/index.html");
            }
            else if (path.equals("/user/login.html")) {
                byte[] body = Files.readAllBytes(Paths.get("./webapp/user/login.html"));
                response200Header(dos, body);
                responseBody(dos, body);
            }

            else if (path.equals("/user/login")) {
                if (method.equals("POST")) {
                    char[] bodyData = new char[contentLength];
                    br.read(bodyData, 0, contentLength);
                    String body = new String(bodyData);
                    Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);

                    String userId = params.get("userId");
                    String password = params.get("password");

                    System.out.println(userId);     // gka365
                    System.out.println(password);   // 1234

                    User existingUser = userDB.findUserById(userId);
                    System.out.println(existingUser);
                    // 로그인 검증
                    if (existingUser != null ) {
                        // 로그인 성공
                        response302HeaderWithCookie(dos, "/index.html"); // 홈으로 리다이렉트
                    } else {
                        // 로그인 실패
                        response302Header(dos, "/user/login_failed.html");
                    }
                }
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, byte[] body) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body);
            dos.flush();
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

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");  //
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
}
