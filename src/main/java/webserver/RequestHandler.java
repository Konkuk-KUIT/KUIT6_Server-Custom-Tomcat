package webserver;

import db.MemoryUserRepository;
import enums.*;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enums.HttpMethod.GET;
import static enums.HttpMethod.POST;


public class RequestHandler implements Runnable {
    Socket connection; // 소켓 - 데이터 주고받기 위한 양쪽 끝 단자
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

            // 요청 라인 읽기 GET /index.html HTTP/1.1
            String request = br.readLine();
            if (request == null) return; // 요청 라인 비어있으면 종료..

            String[] tokens = request.split(" ");
            String method = tokens[0]; // 요청 방식
            String path = tokens[1]; // 요청한 경로

            // 헤더 읽기 (body 길이 확인)
            int requestContentLength = 0;
            boolean isLogined = false;
            while (true) {
                final String line = br.readLine();
                if (line.isEmpty()) {
                    break;
                }
                // header info
                if (HttpHeader.CONTENT_LENGTH.matches(line)) {
                    requestContentLength = Integer.parseInt(line.split(": ")[1].trim());
                }

                if (HttpHeader.COOKIE.matches(line)) {
                    String cookieHeader = line.split(": ")[1]; // "logined=true"
                    if (cookieHeader.contains("logined=true")) {
                        isLogined = true;
                    }
                }
            }

            if (RequestPath.ROOT.matches(path)) {
                path = RequestPath.ROOT.getRedirect();
            }
            HttpMethod httpMethod = HttpMethod.from(method);

            // 회원가입 요청 - GET일때
            if (httpMethod == GET && RequestPath.SIGNUP.matches(path)) {
                String[] urlParts = path.split("\\?"); // path / query 분리 : ?을 기준으로 split 해준다!
                String queryString = urlParts.length > 1 ? urlParts[1] : "";

                Map<String, String> params = parseFormData(queryString);
                saveUser(params);

                // index.html로 리다이렉트
                response302Header(dos, RequestPath.INDEX.getPath());
                return;
            }

            // 회원가입 요청 - POST일때
            if (httpMethod == POST && RequestPath.SIGNUP.matches(path)) {
                String body = readBody(br, requestContentLength); // body를 읽어준다.
                Map<String, String> params = parseFormData(body);
                saveUser(params);
                response302Header(dos, RequestPath.INDEX.getPath());
                return;
            }

            // 로그인 요청
            if (httpMethod == POST && RequestPath.LOGIN.matches(path)) {
                String body = readBody(br, requestContentLength); // body를 읽어준다.
                Map<String, String> params = parseFormData(body);

                String userId = params.get(UserParam.USER_ID.getKey());
                String password = params.get(UserParam.PASSWORD.getKey());

                MemoryUserRepository repository = MemoryUserRepository.getInstance();
                User user = repository.findUserById(userId);

                if (user != null && user.getPassword().equals(password)) { // 로그인 성공했으면
                    response302LoginSuccess(dos, RequestPath.INDEX.getPath());
                } else
                    response302Header(dos, RequestPath.USER_LOGIN_FAILED.getPath());
                return;
            }

            // 사용자 목록 출력
            if (RequestPath.USER_LIST.matches(path)) {
                if (isLogined) { // 로그인 되어있다면 userList.html 반환
                    String filePath = "webapp" + RequestPath.USER_LIST.getPath();
                    byte[] body = Files.readAllBytes(Paths.get(filePath));
                    response200Header(dos, body.length, filePath);
                    responseBody(dos, body);
                } else {
                    response302Header(dos, RequestPath.LOGIN.getPath()+".html");
                }
                return;
            }

            String filePath = "webapp" + path; // URL 경로 → 프로젝트 폴더 경로로 매핑
            byte[] body;
            try {
                body = Files.readAllBytes(Paths.get(filePath));
                response200Header(dos, body.length, filePath); // 응답 헤더: 200 OK
                responseBody(dos, body);             // 응답 바디: 파일 내용
            } catch (IOException e) {
                response404Header(dos);
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    // br : 이미 헤더까지 읽고, 현재 바디 시작 위치에 커서가 있음.
    private String readBody(BufferedReader br, int requestContentLength) throws IOException {
        char[] buf = new char[requestContentLength]; // 요청 바디 길이만큼 맞는 char[] 배열 만들기
        // buf : 읽은 데이터 담을 배열, 0 : buf 쓰기 시작할 위치
        int read = br.read(buf, 0, requestContentLength); // 최대 contentLength만큼 바디 내용 읽어 buf에 저장
        return new String(buf, 0, read); // 배열에 들어있는 내용 String으로 변환해서 반환!
    }

    private static Map<String, String> parseFormData(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&"); // &로 쪼개고
        for (String pair : pairs) {
            String[] keyValues = pair.split("="); // 각 항목들을 = 기준으로 키/값 분리
            if (keyValues.length == 2) {
                params.put(keyValues[0], keyValues[1]);
            }
        }
        return params;
    }

    private static void saveUser(Map<String, String> params) {
        User user = new User(
                params.get(UserParam.USER_ID.getKey()),
                params.get(UserParam.PASSWORD.getKey()),
                params.get(UserParam.NAME.getKey()),
                params.get(UserParam.EMAIL.getKey())
        );

        // 파싱한 데이터로 User 객체 만들어준다.
        MemoryUserRepository.getInstance().addUser(user);
    }

    private void response404Header(DataOutputStream dos) {
        try {
            dos.writeBytes(HttpStatus.NOT_FOUND.toStatusLine());
            dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes("<h1>404 Not Found</h1>");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302LoginSuccess(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(HttpStatus.FOUND.toStatusLine());
            dos.writeBytes(HttpHeader.LOCATION.getValue() + ": " + path + "\r\n");
            dos.writeBytes(HttpHeader.SET_COOKIE.getValue() + ": logined=true\r\n"); // 로그인 여부 쿠키 저장
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes(HttpStatus.FOUND.toStatusLine());            dos.writeBytes(HttpHeader.LOCATION.getValue() + ": " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String path) {
        try {
            dos.writeBytes(HttpStatus.OK.toStatusLine());
            if (path.endsWith(".css")) { // .css 요청이 오면 브라우저가 CSS로 인식해서 스타일 적용
                dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": text/css\r\n");
            } else {
                dos.writeBytes(HttpHeader.CONTENT_TYPE.getValue() + ": text/html;charset=utf-8\r\n");
            }
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.getValue() + ": " + lengthOfBodyContent + "\r\n");
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