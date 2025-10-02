package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection; // 소켓 - 데이터 주고받기 위한 양쪽 끝 단자
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

            String request = br.readLine(); // 요청 라인 읽기 GET /index.html HTTP/1.1
            if(request == null) return; // 요청 라인 비어있으면 종료..

            String[] tokens = request.split(" ");
            String method = tokens[0]; // 요청 방식
            String path = tokens[1]; // 요청한 경로

            if (path.equals("/")) {
                path = "/index.html";
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