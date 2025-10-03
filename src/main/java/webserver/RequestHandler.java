package webserver;

import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            byte[] body = "Hello World".getBytes();

            //요청 읽어오기
            String request = br.readLine(); //한 줄 받아와서 검사
            if (request == null) return;

            String[] tokens = request.split(" ");
            String token = tokens[1]; //2번째 요소가 "/index.html" 과 같은 형식

            System.out.println(token);
            Path path = null;

            if (token.equals("/") || token.equals("/index.html")) {
                //파일 경로 읽어오기
                path = Paths.get("./webapp/index.html");
                //파일 내용을 byte로 변환하여 담아줌
//                body = Files.readAllBytes(path);
            }

            //마찬가지로 다른 html 파일 띄우기
            if (token.equals("/user/form.html")) {
                path = Paths.get("./webapp/user/form.html");
            }
            if (token.equals("/user/list.html")) {
                path = Paths.get("./webapp/user/list.html");
            }
            if (token.equals("/user/login.html")) {
                path = Paths.get("./webapp/user/login.html");
            }
            if (token.equals("/user/login_failed.html")) {
                path = Paths.get("./webapp/user/login_failed.html");
            }

            if (path != null)
                body = Files.readAllBytes(path);

            //get 요청 중 querystring 형식 오는 것을 확인
            String[] queryString = token.split("\\?");
            if(queryString[0].equals("signup")){
                //token="/user/signup?userId=fsfs&password=fsf&name=fsfs&email=sally_0113%40naver.com"
                //들어온 정보를 parsing 하여 User instance 생성

            }

            response200Header(dos, body.length);
            responseBody(dos, body);

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

    private void response302Header(DataOutputStream dos, String path) {

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