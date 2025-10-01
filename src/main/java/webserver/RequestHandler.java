package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{        // Class Runnable: void run();
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        // in: HTTP 요청이 들어오면 GET/POST 요청 라인, 헤더 바디 모두 바이트 형태로 들어있음
        // InputStreamReader: 바이트 -> 캐릭터
        // BufferedReader: 한 줄씩 읽기 가능
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

//            byte[] body = "Hello World".getBytes();
//            response200Header(dos, body.length);
//            responseBody(dos, body);

            // req 쪼개기
            /* br에 담긴 내용
               GET /index.html HTTP/1.1
               Host: localhost:80
               Connection: keep-alive
               User-Agent: Mozilla/5.0 ...
            */

            String requestLine = br.readLine();
            String[] requestTokens = requestLine.split(" ");
            String method = requestTokens[0];   // GET/POST etc
            String path = requestTokens[1];     // 경로 읽어옴

            System.out.println("method: " + method + ", path: " + path);


            if(method.equals("GET")){
                // .html 페이지 요청에 따른 res 만들기
                if(path.endsWith(".html")||path.equals("/")){
                    if(path.equals("/")) path="/index.html";
                    File file = new File("webapp" + path);
                    if(file.exists()){
                        byte[] body = Files.readAllBytes(file.toPath());
                        response200Header(dos, body.length, "text/html;charset=utf-8");
                        responseBody(dos, body);
                    }
                    else{
                        // 404 not found
                    }
                }

                else if(path.endsWith(".css")){
                    File file = new File("webapp" + path);
                    if(file.exists()){
                        byte[] body = Files.readAllBytes(file.toPath());
                        response200Header(dos, body.length, "text/css;");
                        responseBody(dos, body);
                    }
                    else{
                        // 404 not found
                    }
                }

                // 사용자 목록 출력
                else if(path.equals("/user/userList")){
                    Map<String, String> header = new HashMap<>();
                    String line;
                    while((line = br.readLine())!=null){    // 연결 종료 방지용
                        if(line.isEmpty()) break;

                        // 그냥 split(":") 때리면 'Host: localhost:80' 이런거에서 문제 생김
                        int index = line.indexOf(":");
                        String key = line.substring(0, index).trim().toLowerCase();
                        String value = line.substring(index+1).trim();
                        header.put(key, value);
                        System.out.println(key + ": " + value);
                    }
                    if(header.get("cookie")==null){     // key 자체가 없는 것임
                        File file = new File("webapp" + "/user/login.html");
                        if(file.exists()){
                            byte[] body = "".getBytes();
                            response302Header(dos, "/user/login.html", null);
                            responseBody(dos, body);
                        }
                    }
                    else if(header.get("cookie").equals("logined=true")){
                        File file = new File("webapp" + "/user/list.html");
                        if(file.exists()){
                            byte[] body = "".getBytes();
                            response302Header(dos, "/user/list.html", null);
                            responseBody(dos, body);
                        }
                    }
                }
                // GET 방식의 회원가입
                else{
                    // ? 기준으로 자르고
                    String queryString = path.split("\\?")[1];
                    // & 기준으로 자르고
                    // = 기준으로 자르기
                    // 이미 있었다!! -> HttpRequestUtils
                    Map<String, String> parameters = HttpRequestUtils.parseQueryParameter(queryString);
                    String userId = parameters.get("userId");
                    String password = parameters.get("password");
                    String name = parameters.get("name");
                    String email = parameters.get("email");
                    System.out.println("userId: " + userId + ", password: " + password + ", name: " + name + ", email: " + email);


                    if(memoryUserRepository.findUserById(userId)==null){
                        memoryUserRepository.addUser(new User(userId, password, name, email));
                        File file = new File("webapp" + "/index.html");
                        if(file.exists()){
                            byte[] body = "".getBytes();
                            response302Header(dos, "/index.html", null);
                            responseBody(dos, body);
                        }
                    }
                    // 이미 있는 userId의 경우
                    else{
                        System.out.println("이미 있는 userId입니다.");
                        System.out.println("isExist: "+ memoryUserRepository.findUserById(userId).getUserId());
                    }
                }
            }

            // POST 방식의 회원 가입
            /*
            POST {path} HTTP/1.1
            Host: localhost:80
            Content-Type: application/x-www-form-urlencoded
            Content-Length: 13

            userId=sohe&password=1234&name=sh&email=sh%40sh
            * */
            else if(method.equals("POST")){

                // 회원가입
                if(path.equals("/user/signup")){
                    // 당장은 필요 없지만.. 혹시 모르니 header 내용 저장
                    Map<String, String> header = new HashMap<>();
                    String line;
                    while((line = br.readLine())!=null){    // 연결 종료 방지용
                        if(line.isEmpty()) break;

                        // 그냥 split(":") 때리면 'Host: localhost:80' 이런거에서 문제 생김
                        int index = line.indexOf(":");
                        String key = line.substring(0, index).trim().toLowerCase();
                        String value = line.substring(index+1).trim();
                        header.put(key, value);
                    }

                    // String queryString = br.readLine(); body가 '\n'으로 안끝날 수 있어서 권장되지 않음
                    int contentLength = Integer.parseInt(header.get("content-length"));
                    char[] bodyChar = new char[contentLength];
                    int offset = 0;
                    while(offset < contentLength){
                        int r = br.read(bodyChar, offset, contentLength - offset);
                        if(r==-1) break;
                        offset += r;
                    }
                    String queryString = new String(bodyChar);
                    Map<String, String> parameters = HttpRequestUtils.parseQueryParameter(queryString);
                    String userId = parameters.get("userId");
                    String password = parameters.get("password");
                    String name = parameters.get("name");
                    String email = parameters.get("email");
                    System.out.println("userId: " + userId + ", password: " + password + ", name: " + name + ", email: " + email);


                    if(memoryUserRepository.findUserById(userId)==null){
                        memoryUserRepository.addUser(new User(userId, password, name, email));
                        File file = new File("webapp" + "/index.html");
                        if(file.exists()){
                            byte[] body = "".getBytes();
                            response302Header(dos, "/index.html", null);
                            responseBody(dos, body);
                        }
                    }
                    // 이미 있는 userId의 경우
                    else{
                        System.out.println("이미 있는 userId입니다.");
                        System.out.println("isExist: "+ memoryUserRepository.findUserById(userId).getUserId());
                    }
                }

                // 로그인
                // method: POST, path: /user/login
                else if(path.equals("/user/login")){
                    Map<String, String> header = new HashMap<>();
                    String line;
                    while((line = br.readLine())!=null){    // 연결 종료 방지용
                        if(line.isEmpty()) break;

                        // 그냥 split(":") 때리면 'Host: localhost:80' 이런거에서 문제 생김
                        int index = line.indexOf(":");
                        String key = line.substring(0, index).trim().toLowerCase();
                        String value = line.substring(index+1).trim();
                        header.put(key, value);
                    }

                    // String queryString = br.readLine(); body가 '\n'으로 안끝날 수 있어서 권장되지 않음
                    int contentLength = Integer.parseInt(header.get("content-length"));
                    char[] bodyChar = new char[contentLength];
                    int offset = 0;
                    while(offset < contentLength){
                        int r = br.read(bodyChar, offset, contentLength - offset);
                        if(r==-1) break;
                        offset += r;
                    }
                    String queryString = new String(bodyChar);
                    Map<String, String> parameters = HttpRequestUtils.parseQueryParameter(queryString);
                    String userId = parameters.get("userId");
                    String password = parameters.get("password");
                    System.out.println("userId: " + userId + ", password: " + password);

                    // 로그인 로직 어떻게 짜지
                    // userId가 있는지 확인 -> User 객체를 반환 반환 받음
                    // 있다면 해당 userId의 비밀번호가 입력된 비밀번호와 같은지 확인
                    if(memoryUserRepository.findUserById(userId)!=null){    // user 있음
                        User loginUser = memoryUserRepository.findUserById(userId);
                        if(password.equals(loginUser.getPassword())){
                            File file = new File("webapp" + "/index.html");
                            if(file.exists()){
                                // TODO 어떻게 cookie 추가하지
                                byte[] body = "".getBytes();
                                response302Header(dos, "/index.html", "logined=true");
                                responseBody(dos, body);
                            }
                        }
                        // 비밀번호 다름
                        else{
                            File file = new File("webapp" + "/user/login_failed.html");
                            if(file.exists()){
                                byte[] body = "".getBytes();
                                response302Header(dos, "/user/login_failed.html", null);
                                responseBody(dos, body);
                            }
                            else{
                                System.out.println("그런 파일 없소");
                            }
                        }
                    }
                    // 그런 유저 없음
                    else{
                        File file = new File("webapp" + "/user/login_failed.html");
                        if(file.exists()){
                            byte[] body = "".getBytes();
                            response302Header(dos, "/user/login_failed.html", null);
                            responseBody(dos, body);
                        }
                        else{
                            System.out.println("그런 파일 없소");
                        }
                    }
                }
            }

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+ contentType + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 OK \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            if(cookie!=null && !cookie.isEmpty()){
                dos.writeBytes("Set-Cookie: " + cookie + "\r\n");   // 서버 -> 클라이언트 : Set-Cookie
                // 너 왜 index.html에선 안됨? => 트러블 슈팅
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();            // 클라이언트로 전송
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}