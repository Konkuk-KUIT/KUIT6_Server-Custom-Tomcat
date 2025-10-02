package webserver;

import db.MemoryUserRepository;
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

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            // 요청 라인 읽기
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return; // 잘못된 요청이면 무시함
            }
            // 헤더 읽기
            int requestContentLength = 0;
            while (true) {
                final String line = br.readLine();
                if (line == null || line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }

            // 공백으로 나눈 후 path 추출
            String[] parsedLine = requestLine.split(" ");
            String method = parsedLine[0];
            String path = parsedLine[1];
            if (path.equals("/")) {
                path = "/index.html";
            }

            // 회원가입 처리
            if (path.startsWith("/user/signup")) {
                if (method.equals("GET")) { // [요구사항 2] 회원가입 요청일 경우
                    String queryString = null;
                    int idx = path.indexOf("?");
                    if (idx != -1) {
                        queryString = path.substring(idx + 1);
                        path = path.substring(0, idx);
                    }

                    if (queryString != null) {
                        Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);

                        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                        MemoryUserRepository.getInstance().addUser(user);
                    }

                    response302Header(dos, "/index.html");
                    return;

                } else if (method.equals("POST")) { // [요구사항 3] post방식의 회원가입일 경우
                    String body = IOUtils.readData(br, requestContentLength);

                    if (body != null && !body.isEmpty()) {
                        Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                        User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                        MemoryUserRepository.getInstance().addUser(user);
                    }
                    response302Header(dos, "/index.html");
                    return;
                }
            }


            // [요구사항 1] 정적 파일 요청일 경우
//            byte[] body = "Hello World".getBytes();
            File file = new File("./webapp" + path);
            if (file.exists()) {
                //byte[] body = Files.readAllBytes(file.toPath());
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] body = fis.readAllBytes();
                    response200Header(dos, body.length, getContentType(path));
                    responseBody(dos, body);
                }
            } else {
                byte[] body = "<h1>404 Not Found</h1>".getBytes();
                response404Header(dos, body.length);
                responseBody(dos, body);
            }


        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html;charset=utf-8";
        if (path.endsWith(".css")) return "text/css;charset=utf-8";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".gif")) return "image/gif";
        return "application/octet-stream"; // 알 수 없는 경우 바이너리로 처리
    }

    // [200 OK]
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

    // [404 Not Found]
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

    // [302 Found]
    private void response302Header(DataOutputStream dos, String redirectPath) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectPath + "\r\n");
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