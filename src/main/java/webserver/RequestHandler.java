package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
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

            // HTTP 요청의 첫 줄 읽기
            String requestLine = br.readLine();
            log.log(Level.INFO, "Request Line: " + requestLine);

            // 예외 처리
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            // RequestLine에서 URL 추출
            String[] tokens = requestLine.split(" "); // 공백을 기준으로 구분
            String method = tokens[0];  // GET, POST 등
            String url = tokens[1];     // /index.html, /user/form.html 등

            log.log(Level.INFO, "Requested URL: " + url);

            // url에서 쿼리스트링 분리
            String path = url;
            String queryString = "";

            if (url.contains("?")) {
                String[] urlParts = url.split("\\?");
                path = urlParts[0];
                queryString = urlParts[1];
            }

            if (path.equals("/")) {
                path = "/index.html";
            }

            log.log(Level.INFO, "Path: " + path);
            log.log(Level.INFO, "QueryString: " + queryString);

            // 회원가입 처리
            if (path.equals("/user/signup")) {
                //쿼리스트링 파싱
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(queryString);

                // User 객체 생성
                User user = new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                );

                // MemoryUserRepository에 저장
                MemoryUserRepository repository = MemoryUserRepository.getInstance();
                repository.addUser(user);

                log.log(Level.INFO, "New User Added: " + user.getUserId());

                // 302 리다이렉트로 index.html로 이동
                response302Header(dos, "/index.html");
                return;
            }

            // favicon, devtools 관련 요청은 로그 출력 안 함
            if (path.equals("/favicon.ico") || path.contains("/.well-known/")) {
                response404(dos);
                return;
            }


            // webapp 폴더에서 파일 읽기
            String filePath = "./webapp" + path;
            File file = new File(filePath);

            if (file.exists() && !file.isDirectory()) {
                // 파일이 존재하면 파일 내용 읽기
                byte[] body = Files.readAllBytes(file.toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else {
                // 파일이 없으면 404 응답
                response404(dos);
            }

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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 404 에러
    private void response404(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes("<h1>404 Not Found</h1>");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 302 리다이렉트
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

}