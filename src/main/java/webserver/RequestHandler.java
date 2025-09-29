package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
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

            String request = br.readLine();
            if(request == null) {
                return;
            }
            log.info("Request: " + request);
            String[] tokens = request.split(" ");
            String url = tokens[1];

            if (url.equals("/")) {
                url = "/index.html";
            }

            Path filePath = Paths.get("./webapp" + url);
            byte[] body;

            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                // 파일이 존재하면 파일 내용을 읽어옵니다.
                body = Files.readAllBytes(filePath);
                // 200 OK 응답 헤더를 작성합니다.
                response200Header(dos, body.length);
            } else {
                // 파일이 존재하지 않으면 404 Not Found 응답을 보냅니다.
                body = "404 Not Found".getBytes();
                // 404 Not Found 응답 헤더를 작성합니다.
            }

            responseBody(dos, body);


//            byte[] body = "Hello World".getBytes();
//            response200Header(dos, body.length);
//            responseBody(dos, body);

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