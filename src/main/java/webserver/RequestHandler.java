package webserver;

import db.MemoryUserRepository;
import http.util.IOUtils;
import model.User;

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

            String request = br.readLine(); //한 줄 받아와서 검사
            if (request == null) return;

            String[] tokens = request.split(" ");
            String method = tokens[0]; //get/post
            String uri = tokens[1]; //2번째 요소가 "/index.html" 과 같은 형식
            String params = null;

            System.out.println(uri);
            Path path = null;

            //GET 요청 읽어오기
            if (uri.equals("/") || uri.equals("/index.html")) {
                //파일 경로 읽어오기
                path = Paths.get("./webapp/index.html");
                //파일 내용을 byte로 변환하여 담아줌
            }

            //마찬가지로 다른 html 파일 띄우기
            if (uri.equals("/user/form.html")) {
                path = Paths.get("./webapp/user/form.html");
            }
            if (uri.equals("/user/list.html")) {
                path = Paths.get("./webapp/user/list.html");
            }
            if (uri.equals("/user/login.html")) {
                path = Paths.get("./webapp/user/login.html");
            }
            if (uri.equals("/user/login_failed.html")) {
                path = Paths.get("./webapp/user/login_failed.html");
            }


            //post 요청 읽어오기
            int requestContentLength = 0;
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }
//            IOUtils ioUtils = new IOUtils(br, requestContentLength);
            if(method.equals("POST")){
                IOUtils ioUtils = IOUtils.getInstance();
                params = ioUtils.readData(br, requestContentLength); //"userId=abc&password=123&name=kim" 를 반환함
            }
            if(method.equals("GET")&&uri.contains("?")){
                params = uri.split("\\?")[1];
            }


            //get 요청 중 querystring 형식 오는 것을 확인
            //uri="/user/signup?userId=fsfs&password=fsf&name=fsfs&email=sally_0113%40naver.com"
            String[] queryString = uri.split("\\?");

            if(uri.startsWith("/user/signup")){
                //들어온 정보를 parsing 하여 User instance 생성
                String[] userInfo = params.split("[&=]");
                //userInfo[] => userId, fsfs, password, fsf, name, fsfs, email, sally~
                //public User(String userId, String password, String name, String email) {
                String userId = userInfo[1];
                String password = userInfo[3];
                String name = userInfo[5];
                String email = userInfo[7];
                User user = new User(userId, password, name, email);

                //만든 user instance 를 MemoryUserRepository 에 저장 후 index.html 반환
                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
                memoryUserRepository.addUser(user);
                //302 로 리다이렉트
                response302Header(dos, "/index.html");
            }

            if (path != null)
                body = Files.readAllBytes(path);
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
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (Exception e) {
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