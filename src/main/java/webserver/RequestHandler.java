package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
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

            // URL이 "/"인 경우 "/index.html"로 변경
            if (url.equals("/")) {
                url = "/index.html";
            }

            log.log(Level.INFO, "Requested URL: " + url);

            // webapp 폴더에서 파일 읽기
            String filePath = "./webapp" + url;
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

}