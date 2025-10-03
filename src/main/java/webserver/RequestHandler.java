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
            MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance(); //싱글톤 생성

            String request = br.readLine(); //한 줄 받아와서 검사
            if (request == null) return;

            String[] tokens = request.split(" ");
            String method = tokens[0]; //get/post
            String uri = tokens[1]; //2번째 요소가 "/index.html" 과 같은 형식
            String params = null; //회원가입 시 queryString 저장용 변수

            System.out.println(uri);
            Path path = null;
            String cookie = null;
            String contentType = "html";

            //확장자에 따른 contentType 설정
            if (uri.endsWith(".css")) {
                contentType = "css";
            }

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
//            if (uri.equals("/user/list.html")) { //브라우저에 이렇게 입력하면 직접 GET 요청을 준 것.
//                path = Paths.get("./webapp/user/list.html");
//            }
            if (uri.equals("/user/login.html")) {
                path = Paths.get("./webapp/user/login.html");
            }
            if (uri.equals("/user/login_failed.html")) {
                path = Paths.get("./webapp/user/login_failed.html");
            }

            //header 읽어오기
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
                if (line.startsWith("Cookie")) {
                    cookie = line.split(": ")[1];   //"logined=true"
                }
            }
//            IOUtils ioUtils = new IOUtils(br, requestContentLength);
            if (method.equals("POST")) {
                IOUtils ioUtils = IOUtils.getInstance();
                params = ioUtils.readData(br, requestContentLength); //"userId=abc&password=123&name=kim"  반환
            }
            if (method.equals("GET") && uri.contains("?")) {
                params = uri.split("\\?")[1];
            }

            //uri="/user/signup?userId=fsfs&password=fsf&name=fsfs&email=sally_0113%40naver.com"
            if (uri.startsWith("/user/signup")) {
                //들어온 정보를 parsing 하여 User instance 생성
                String[] userInfo = params.split("[&=]");
                //userInfo[] => userId, fsfs, password, fsf, name, fsfs, email, sally~
                String userId = userInfo[1];
                String password = userInfo[3];
                String name = userInfo[5];
                String email = userInfo[7];
                User user = new User(userId, password, name, email);

                //만든 user instance 를 MemoryUserRepository 에 저장 후 index.html 반환
                memoryUserRepository.addUser(user);
                //302 로 리다이렉트
                response302Header(dos, "/index.html", null);
                return;
            }

            //uri="/user/login?userId=fsfs&password=fsf (get인 경우)
            /*uri이 /user/login/html 인 경우에도 이와 동일하므로 구분해주어야 함
            => 화면 요청하는 건 GET, 로그인 동작 시에는 POST */
            if (uri.equals("/user/login") && method.equals("POST")) {
                //params = "userId=fsfs&password=fsf"
                String[] userInfo = params.split("[&=]");
//                userInfo=> userId, fsfs, password, fsf 담고 있음
                User newUser = null;
                newUser = memoryUserRepository.findUserById(userInfo[1]);
                if (newUser != null) { //해당 유저가 존재하면
                    //header에 Cookie 추가
//                    dos.writeBytes("Set-Cookie: logined=true\r\n");
                    response302Header(dos, "/index.html", "logined=true");
                    return;
                } else {
                    //존재하지 않으면 login_failed 로 돌아감
                    path = Paths.get("./webapp/user/login_failed.html");

                }
            }

            if (uri.equals("/user/userList")) {
                //header의 Cookie가 logined=true 일 때만
                if (cookie != null && cookie.contains("logined=true")) {
                    path = Paths.get("./webapp/user/list.html");
                } else {
                    response302Header(dos, "/user/login.html", null); //로그인 되지 않은 화면으로 리다렉트
                    return;
                }
            }

            //css 적용
            if (uri.startsWith("./css")) {
                path = Paths.get("./webapp/css/styles.css");
                contentType = "css";
            }

            if (path != null)
                body = Files.readAllBytes(path);

                response200Header(dos, body.length, contentType);
            responseBody(dos, body);


        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/" + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            if (cookie != null) {
                dos.writeBytes("Set-Cookie: " + cookie + "; Path=/\r\n");
            }
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