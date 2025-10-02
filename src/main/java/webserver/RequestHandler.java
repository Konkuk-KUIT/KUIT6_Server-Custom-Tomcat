package webserver;

import db.MemoryUserRepository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    Socket connection; // 소켓 - 데이터 주고받기 위한 양쪽 끝 단자
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

            String request = br.readLine(); // 요청 라인 읽기 GET /index.html HTTP/1.1
            if (request == null) return; // 요청 라인 비어있으면 종료..

            String[] tokens = request.split(" ");
            String method = tokens[0]; // 요청 방식
            String path = tokens[1]; // 요청한 경로

            if (path.equals("/")) {
                path = "/index.html";
            }

            // 회원가입 요청인지 확인
            if (method.equalsIgnoreCase("GET") && path.startsWith("/user/signup")) {
                String[] urlParts = path.split("\\?"); // path / query 분리 : ?을 기준으로 split 해준다!
                String queryString = urlParts.length > 1 ? urlParts[1] : "";

                Map<String, String> params = parseFormData(queryString);
                saveUser(params);

                // index.html로 리다이렉트
                response302Header(dos, "/index.html");
                return;
            }

            String filePath = "webapp" + path; // URL 경로 → 프로젝트 폴더 경로로 매핑

            byte[] body;
            try {
                body = Files.readAllBytes(Paths.get(filePath));
                response200Header(dos, body.length); // 응답 헤더: 200 OK
                responseBody(dos, body);             // 응답 바디: 파일 내용
            } catch (IOException e) {
                // 파일이 없으면 404 응답을 해주자.
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private static Map<String, String> parseFormData(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&"); // &로 쪼개고
        for (String pair : pairs) {
            String[] keyValues = pair.split("="); // 각 항목들을 = 기준으로 키/값 분리
            if (keyValues.length == 2) {
                params.put(keyValues[0], keyValues[1]);
            }
        }
        return params;
    }

    private static void saveUser(Map<String, String> params) {
        User user = new User(
                params.get("userId"),
                params.get("password"),
                params.get("name"),
                params.get("email")
        );

        // 파싱한 데이터로 User 객체 만들어준다.
       MemoryUserRepository.getInstance().addUser(user);
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path +"\r\n");
            dos.writeBytes("\r\n");
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

}