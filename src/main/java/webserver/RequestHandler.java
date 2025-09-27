package webserver;

import java.io.*;
import java.net.Socket;
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
            if (fullPath.contains("?")) {
                path = fullPath.substring(0, fullPath.indexOf("?"));
            }
            log.log(Level.INFO, "Method: " + method + ", Path: " + path + ", Version: " + httpVersion);

            // 경로에 따른 파일 매핑 로직
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}