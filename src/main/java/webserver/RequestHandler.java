package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;

import db.MemoryUserRepository;
import http.util.IOUtils;
import http.util.HttpRequestUtils;
import model.User;

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
            BufferedReader br = new BufferedReader(new InputStreamReader(in)); // 라인 단위 읽기 기능 추가
            DataOutputStream dos = new DataOutputStream(out); // 바이트 단위 쓰기 기능 추가

            // HTTP 요청 라인 읽기 -> etc: "GET /index.html HTTP/1.1"
            String requestLine = br.readLine();
            log.log(Level.INFO, "Request Line: " + requestLine);

            // 요청 라인이 비어있으면 연결 종료
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }

            // HTTP 요청 라인을 공백으로 분리
            // parts[0]: HTTP 메서드 (GET, POST 등)
            // parts[1]: 요청 경로 (/index.html, /users/form.html +..)
            // parts[2]: HTTP 버전 (HTTP/3)
            String[]  requestParts = requestLine.split(" ");
            if (requestParts.length != 3) {
                log.log(Level.WARNING, "Invalid Request Line: " + requestLine);
            }

            String method = requestParts[0];
            String fullPath = requestParts[1];
            String httpVersion = requestParts[2];

            // 쿼리 파라미터 제거 (예: /user/form.html?name=john -> /user/form.html
            String path = fullPath;
            String queryString = null;
            if (fullPath.contains("?")) {
                path = fullPath.substring(0, fullPath.indexOf("?"));
                queryString = fullPath.substring(fullPath.indexOf("?") + 1);
            }
            log.log(Level.INFO, "Path: " + path + ", Query String: " + queryString);
            log.log(Level.INFO, "Method: " + method + ", Path: " + path + ", Version: " + httpVersion);

            // HTTP 헤더들 읽기 (빈 라인까지)
            String headerLine;
            int contentLength = 0;
            while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
                log.log(Level.INFO, "Header: " + headerLine);
                
                // Content-Length 헤더 파싱
                if (headerLine.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(headerLine.substring(15).trim());
                }
            }
            log.log(Level.INFO, "Headers read complete. Content-Length: " + contentLength);

            // POST 요청의 바디 데이터 읽기
            String requestBody = null;
            if ("POST".equals(method) && contentLength > 0) {
                requestBody = IOUtils.readData(br, contentLength);
                log.log(Level.INFO, "Request body: " + requestBody);
            }

            // 경로에 따른 파일 매핑 로직
            // 회원 가입 처리
            if (path.equals("/user/signup") && (queryString != null || "POST".equals(method))) {
                // queryString parsing
                Map<String, String> params;

                if ("POST".equals(method)) {
                    // body에서 파라미터 추출
                    params = HttpRequestUtils.parseQueryParameter(requestBody);
                    log.log(Level.INFO, "POST Signup params: " + params);
                } else {
                    // GET에서 파라미터 추출
                    params = HttpRequestUtils.parseQueryParameter(queryString);
                    log.log(Level.INFO, "GET Signup params: " + params);
                }

                // User 객체 생성
                User newUser = new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                );

                // 메모리 저장소에 저장
                MemoryUserRepository repository = MemoryUserRepository.getInstance();
                repository.addUser(newUser);
                log.log(Level.INFO, "New User Registered: " + newUser.getUserId());

                // 302 리다이렉트로 메인 페이지로 이동
                response302Header(dos);
                return;
            }

            // 1. 루트 경로 ("/") 처리 - 기본 페이지로 리다이렉트
            if (path.equals("/")) {
                path = "/index.html";
            }

            // 2. 보안 검증 - ../ 과 같은 디렉토리 traversal 공격 방지
            if (path.contains("..")) {
                log.log(Level.WARNING, "Path contains invalid path: " + path);
                // TODO: 에러 반환
                return;
            }

            // 3. webapp 폴더 기준으로 실제 파일 경로 생성
            String filePath = "webapp" + path;
            log.log(Level.INFO, "File path: " + filePath);

            try {
                // 4. 파일 존재 여부 확인 및 읽기
                byte[] fileContent = Files.readAllBytes(Paths.get(filePath));
                log.log(Level.INFO, "File read successfully: " + filePath);
                
                // 5. 성공적으로 읽었으면 200 OK 응답
                response200Header(dos, fileContent.length);
                responseBody(dos, fileContent);
                
            } catch (IOException fileException) {
                // 6. 파일이 없거나 읽기 실패시 로그 기록
                log.log(Level.WARNING, "File not found or read error: " + filePath);
                // TODO: 404 에러 응답 구현
                byte[] errorBody = "404 Not Found".getBytes();
                response200Header(dos, errorBody.length);
                responseBody(dos, errorBody);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
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

    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + "/index.html" + "\r\n");
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