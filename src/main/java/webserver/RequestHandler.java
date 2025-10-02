package webserver;

import db.MemoryUserRepository;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);

            String requestLine = br.readLine();
            if (requestLine == null) {
                return;
            }

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String url = tokens[1];
            int contentLength = 0;
            boolean isLoggedIn = false;

            String headerLine;
            while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
                if (headerLine.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                }
                // 쿠키 헤더를 확인하여 로그인 상태 파악
                if (headerLine.startsWith("Cookie")) {
                    String cookieValue = headerLine.split(":")[1].trim();
                    if (cookieValue.contains("logined=true")) {
                        isLoggedIn = true;
                    }
                }
            }

            // 1. POST /user/login (로그인 처리)
            if (method.equals("POST") && url.equals("/user/login")) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = parseQueryString(body);
                User user = MemoryUserRepository.getInstance().findUserById(params.get("userId"));

                // 사용자가 존재하고 비밀번호가 일치하는 경우
                if (user != null && user.getPassword().equals(params.get("password"))) {
                    log.log(Level.INFO, "Login successful for user: " + user.getUserId());
                    response302WithCookie(dos, "/index.html"); // 쿠키와 함께 리다이렉트
                } else {
                    log.log(Level.INFO, "Login failed for user ID: " + params.get("userId"));
                    response302Header(dos, "/user/login_failed.html"); // 실패 페이지로 리다이렉트
                }

                // 2. POST /user/signup (회원가입 처리)
            } else if (method.equals("POST") && url.equals("/user/signup")) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = parseQueryString(body);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                MemoryUserRepository.getInstance().addUser(user);
                response302Header(dos, "/index.html");

                // 3. GET /user/list.html (사용자 목록 - 로그인 필요)
            } else if (method.equals("GET") && url.equals("user/userList")) {
                if (isLoggedIn) {
                    // 로그인 상태이면 list.html 파일 제공
                    serveStaticFile(dos, "user/list.html");
                } else {
                    // 비로그인 상태이면 login.html로 리다이렉트
                    response302Header(dos, "user/login.html");
                }

                // 4. 그 외 GET 요청 (정적 파일 처리)
            } else if (method.equals("GET")) {
                serveStaticFile(dos, url);

                // 5. 지원하지 않는 요청
            } else {
                byte[] body = "405 Method Not Allowed".getBytes();
                response404Header(dos, body.length);
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    /**
     * 로그인 성공 시 Set-Cookie 헤더를 포함하여 302 응답을 보냅니다.
     */
    private void response302WithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true; Path=/\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    /**
     * 정적 파일을 읽어 200 OK 응답 또는 404 Not Found 응답을 보냅니다.
     */
    private void serveStaticFile(DataOutputStream dos, String url) {
        try {
            if (url.equals("/")) {
                url = "/index.html";
            }

            // 기본 Content-Type을 HTML로 설정
            String contentType = "text/html;charset=utf-8";
            // URL이 .css로 끝나면 Content-Type을 text/css로 변경
            if (url.endsWith(".css")) {
                contentType = "text/css";
            }

            Path filePath = Paths.get("./webapp" + url);
            byte[] body = Files.readAllBytes(filePath);

            // 결정된 Content-Type으로 헤더 응답
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
        } catch (IOException e) {
            byte[] body = "404 Not Found".getBytes();
            response404Header(dos, body.length);
            responseBody(dos, body);
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            return params;
        }
        try {
            String decoded = URLDecoder.decode(queryString, "UTF-8");
            String[] pairs = decoded.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx > 0) {
                    params.put(pair.substring(0, idx), pair.substring(idx + 1));
                }
            }
        } catch (UnsupportedEncodingException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
        return params;
    }
}