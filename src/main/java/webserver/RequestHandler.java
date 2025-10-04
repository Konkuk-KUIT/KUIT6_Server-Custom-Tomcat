package webserver;

import http.request.HttpRequest;
import http.response.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
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
        log.log(Level.INFO, "New Client Connect! IP: " + connection.getInetAddress() + ", Port: " + connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(out);

            RequestMapper requestMapper = new RequestMapper(httpRequest, httpResponse);
            requestMapper.proceed();

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}

/*
package webserver;

import db.MemoryUserRepository;
import model.User;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    //서버 소켓이 클라이언트를 받아들이면 생성되는 소켓을 매개변수로 받아옴.
    //이 소켓을 통해 클라이언트, 서버 간 통신이 이루어짐.
    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    //Runnable 인터페이스 구현에 따른 메서드
    //새로운 클라이언트가 접속했을 때 호출되어, 요청을 처리하고 응답을 보내는 전체흐름을 담당.
    @Override
    public void run() {
        //로그 기록: 어떤 클라이언트가 연결되었는지(IP,Port)출력.
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        //Stream 열기
        //InputStream in : 클라이언트가 보낸 데이터를 읽기 위한 입력 스트림
        //OutputStream out : 서버가 클라이언트에게 보낼 데이터를 작성하기 위한 출력 스트림
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){

            //BufferedReader br : 클라이언트 요청 메시지를 문자 단위로 읽음
            //DataOutputStream dos : HTTP 응답을 바이트 단위로 클라이언트에 전송
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);
            // 1. 요청 라인 읽기
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isEmpty()) return;

            // 2. 요청 라인 파싱
            String[] tokens = requestLine.split(" ");
            String method = tokens[0];

            String[] pathParts = tokens[1].split("\\?", 2); // '?' 기준으로 분리
            String fullPath = pathParts[0]; // 쿼리스트링 제거된 URL 경로
            if (fullPath.equals("/favicon.ico")) {
                dos.writeBytes("HTTP/1.1 204 No Content\r\n\r\n");
                dos.flush();
                return;
            }
            int contentLength = 0;
            // 헤더 읽기 및 Content-Length 값 구하기
            String line;
            while (!(line = br.readLine()).equals("")) {
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }

            */
/*
            if (method.equals("POST") && fullPath.equals("/user/signup")) {
                // contentLength 만큼 본문 읽기
                char[] bodyChars = new char[contentLength];
                int readLen = br.read(bodyChars, 0, contentLength);
                String body = new String(bodyChars, 0, readLen);

                // 쿼리스트링 파싱
                Map<String, String> paramMap = parseQueryString(body);

                String userId = paramMap.getOrDefault("userId", "");
                String password = paramMap.getOrDefault("password", "");
                String name = paramMap.getOrDefault("name", "");
                String email = paramMap.getOrDefault("email", "");

                User newUser = new User(userId, password, name, email);
                MemoryUserRepository.getInstance().addUser(newUser);

                // 302 리다이렉트
                response302Header(dos, "/index.html");
                return;
            }
            *//*


            // GET 방식 회원가입 처리
            if (method.equals("GET") && fullPath.startsWith("/user/signup")) {
                String[] pathPart = fullPath.split("\\?", 2);
                if (pathPart.length == 2) {
                    String queryString = pathPart[1];
                    Map<String, String> paramMap = parseQueryString(queryString);

                    // User 필드 맞춰 파싱
                    String userId = paramMap.getOrDefault("userId", "").trim();
                    String password = paramMap.getOrDefault("password", "").trim();
                    String name = paramMap.getOrDefault("name", "").trim();
                    String email = paramMap.getOrDefault("email", "").trim();

                    User newUser = new User(userId, password, name, email);
                    MemoryUserRepository.getInstance().addUser(newUser);
                    System.out.println("[회원가입] userId: " + userId + ", password: " + password);
                    System.out.println("[현재 저장된 회원]: " + MemoryUserRepository.getInstance().findUserById(userId));
                }
                // 302 리다이렉트
                response302Header(dos, "/index.html");
                return;
            }

            */
/*
            // GET 회원가입 폼 요청은 /user/form.html로 처리
            if (method.equals("GET") && fullPath.equals("/user/form")) {
                String formPage = "webapp/user/form.html";
                if (Files.exists(Paths.get(formPage))) {
                    byte[] body = Files.readAllBytes(Paths.get(formPage));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } else {
                    // 404 처리
                }
                return;
            }
            *//*

            if (method.equals("POST") && fullPath.equals("/user/signup")) {
                */
/*String formPage = "webapp/user/form.html";
                if (Files.exists(Paths.get(formPage))) {
                    byte[] body = Files.readAllBytes(Paths.get(formPage));
                    response302Header(dos, "/index.html");
                    responseBody(dos, body);
                } else {
                    // 404 처리
                }
                return;*//*

                char[] bodyChars = new char[contentLength];
                int readLen = br.read(bodyChars, 0, contentLength);
                String body = new String(bodyChars, 0, readLen);

                Map<String, String> paramMap = parseQueryString(body);

                    // User 필드 맞춰 파싱
                String userId = paramMap.getOrDefault("userId", "").trim();
                String password = paramMap.getOrDefault("password", "").trim();
                String name = paramMap.getOrDefault("name", "").trim();
                String email = paramMap.getOrDefault("email", "").trim();

                User newUser = new User(userId, password, name, email);
                MemoryUserRepository.getInstance().addUser(newUser);
                System.out.println("[회원가입] userId: " + userId + ", password: " + password);
                System.out.println("[현재 저장된 회원]: " + MemoryUserRepository.getInstance().findUserById(userId));

                // 302 리다이렉트
                response302Header(dos, "/index.html");
                return;
            }

            // POST 로그인 처리
            if (method.equals("POST") && fullPath.equals("/user/login")) {
                char[] bodyChars = new char[contentLength];
                int readLen = br.read(bodyChars, 0, contentLength);
                String body = new String(bodyChars, 0, readLen);

                Map<String, String> paramMap = parseQueryString(body);
                String userId = paramMap.getOrDefault("userId", "").trim();
                String password = paramMap.getOrDefault("password", "").trim();

                User user = MemoryUserRepository.getInstance().findUserById(userId);
                if (user != null && user.getPassword().trim().equals(password)) {
                    // 로그인 성공 - 쿠키 설정, 리다이렉트 index.html
                    response302HeaderWithCookie(dos, "/index.html");
                } else {
                    // 로그인 실패 - login_failed.html로 리다이렉트
                    response302Header(dos, "/user/login_failed.html");
                }
                return;
            }
            // GET 로그인 폼 요청 처리
            if (method.equals("GET") && fullPath.equals("/user/login")) {
                String loginPage = "webapp/user/login.html";
                if (Files.exists(Paths.get(loginPage))) {
                    byte[] body = Files.readAllBytes(Paths.get(loginPage));
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                } else {
                    byte[] body = "<h1>Login page not found</h1>".getBytes();
                    response200Header(dos, body.length);
                    responseBody(dos, body);
                }
                return;
            }
            // "/"일 땐 자동으로 index.html로 변환
            if (fullPath.equals("/")) {
                fullPath = "/index.html";
            }

            // 디렉토리 접근시 마지막에 '/'로 끝나면 index.html로 변환
            if (fullPath.endsWith("/")) {
                fullPath += "index.html";
            }

            // 파일 경로 처리
            //Path filePathObj = Paths.get("webapp", fullPath);
            //String filePath = filePathObj.toString(); // index.html이 target됨
            //System.out.println("Requested file path: " + filePath);
            String filePath = "webapp" + fullPath;
            // 확장자별 Content-Type 지정
            String contentType = "text/html;charset=utf-8";
            if (filePath.endsWith(".css")) {
                contentType = "text/css";
            } else if (filePath.endsWith(".js")) {
                contentType = "application/javascript";
            } // 필요한 타입 추가

            // 3. 경로에 따라 응답 본문 결정
            byte[] body;
            // 앞의 '/' 제거: 파일 경로 오동작 방지
            if (Files.exists(Paths.get(filePath)) && !Files.isDirectory(Paths.get(filePath))) {
                //System.out.println("File exists: " + filePath);
                body = Files.readAllBytes(Paths.get(filePath));
            } else {
                //System.out.println("File NOT exists: " + filePath);
                body = "<html><body>404 Not Found</body></html>".getBytes();
                contentType = "text/html;charset=utf-8";
            }


            // 4. 응답 헤더 및 본문 전송
            response200Header(dos, body.length);
            responseBody(dos, body);

            //응답 작성:
            //response200Header() 호출 ->HTTP응답 헤더 작성
            //responseBody() 호출 -> 실제 응답 데이터("Hello World") 전송
            //byte[] body = "Hello World".getBytes();
            //response200Header(dos, body.length);
            //responseBody(dos, body);
            */
/*if (fullPath.equals("/favicon.ico")) {
                dos.writeBytes("HTTP/1.1 204 No Content\r\n\r\n");
                dos.flush();
                return;
            }*//*


        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    // 302 리다이렉트 + 쿠키 추가 메서드
    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " +path+ "\r\n");
            dos.writeBytes("Set-Cookie: logined=true; Path=/\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // 쿼리스트링 파싱
    private Map<String, String> parseQueryString(String query) {
        Map<String, String> paramMap = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2){
                try {
                    paramMap.put(
                            java.net.URLDecoder.decode(keyValue[0], "UTF-8"),
                            java.net.URLDecoder.decode(keyValue[1], "UTF-8")
                    );
                } catch (Exception e) {
                    // 만약 디코딩이 실패하면 원본 값을 그대로 사용
                    paramMap.put(keyValue[0], keyValue[1]);
                }
            }

        }
        return paramMap;
    }
    //클라이언트에게 HTTP 응답 헤더를 전송.
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            //상태 코드 전송
            //헤더 정보 작성
            //Content-Type: 응답본문이 HTML이며 UTF-8인코딩 사용함을 알림.
            //Content-Length-> 본문의 바이트 크기를 지정("Hello World 길이")
            // \r\n은 헤더와 본문을 구분하는 빈 줄을 의미.
            dos.writeBytes("HTTP/1.1 200 OK \r\n");

            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    //HTTP 응답 본문을 클라이언트에게 전송
    //dos.write()로 바이트 배열("Hello World")을 실제로 보냄
    //dos.flush()로 버퍼에 남아있는 데이터까지 모두 전송
    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}

*/

/*

전체 동작 흐름
클라이언트가 서버에 접속 → Socket 생성. Socket 이란 결국 데이터 통로 누가 Socket에 적으면 다른쪽에서 읽는다.

RequestHandler의 run() 실행.

서버는 클라이언트 요청을 읽을 수 있는 상태가 됨. (현 코드는 요청 분석은 생략하고 무조건 "Hello World" 응답) ->수정

response200Header()로 HTTP 응답 헤더 작성.

responseBody()로 본문 전송.

클라이언트는 브라우저에서 "Hello World" 화면 확인 가능

*/
