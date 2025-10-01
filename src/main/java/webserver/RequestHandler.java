package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }
    boolean isCorrectPassword;
    boolean isLoggedIn = false;
    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

//            for (Object o :  br.lines().toArray()) {
//                String line = (String) o;
//                if (line.equals("")) {
//                    break;
//                }
//                System.out.println("line = " + line);
//            }
//                line = GET /hello?hello=123 HTTP/1.1
//                line = Host: localhost
//                line = Connection: keep-alive
//                line = Cache-Control: max-age=0
//                line = sec-ch-ua: "Chromium";v="140", "Not=A?Brand";v="24", "Google Chrome";v="140"
//                line = sec-ch-ua-mobile: ?0
//                line = sec-ch-ua-platform: "Windows"
//                line = Upgrade-Insecure-Requests: 1
//                line = User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36
//                line = Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7
//                line = Sec-Fetch-Site: none
//                line = Sec-Fetch-Mode: navigate
//                line = Sec-Fetch-User: ?1
//                line = Sec-Fetch-Dest: document
//                line = Accept-Encoding: gzip, deflate, br, zstd
//                line = Accept-Language: ko,en-US;q=0.9,en;q=0.8,fr;q=0.7,lg;q=0.6
//                line = Cookie: Idea-ff308f81=d534b178-568b-483e-b938-02a6d9526049

            Map<String, String> stringStringMap = HttpRequestUtils.parseRequestLine(br.readLine());

            String method = stringStringMap.get("method");
            String path = stringStringMap.get("path");
            String queryString = stringStringMap.get("queryString");
//            System.out.println("method = " + method);
//            System.out.println("path = " + path);
//            Map<String, String> paramMap = HttpRequestUtils.parseQueryParams(queryString);
//            System.out.println("method = " + method);
//            System.out.println("path = " + path);
//            System.out.println("paramMap = " + paramMap);
            if(Objects.equals(path, "/")){
                path="index.html";
            }

            else if(Objects.equals(path, "/user/signup") && Objects.equals(method,"POST")){
                int contentLength = 0;
                while (true) {
                    final String line = br.readLine();
                    if (line.isEmpty()) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Content-Length")) {
                        contentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }

                char[] bodyData = new char[contentLength];
                br.read(bodyData, 0, contentLength);

                String body = new String(bodyData);
                Map<String, String> bodyParams = HttpRequestUtils.parseQueryParams(body);

                User user = new User(bodyParams.get("userId"),
                        bodyParams.get("password"),
                        bodyParams.get("name"),
                        bodyParams.get("email"));
                MemoryUserRepository.getInstance().addUser(user);
            }
            else if(Objects.equals(path, "/user/login") && Objects.equals(method,"POST")){
                int contentLength = 0;
                while (true) {
                    final String line = br.readLine();
                    if (line.isEmpty()) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Content-Length")) {
                        contentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }

                char[] bodyData = new char[contentLength];
                br.read(bodyData, 0, contentLength);

                String body = new String(bodyData);
                Map<String, String> bodyParams = HttpRequestUtils.parseQueryParams(body);
                String userId = bodyParams.get("userId");
                String password = bodyParams.get("password");
//                System.out.println("userId = " + userId);
//                System.out.println("password = " + password);
                User userById = MemoryUserRepository.getInstance().findUserById(userId);
                isCorrectPassword = userById != null && userById.getPassword().equals(password);
//                System.out.println("isCorrectPassword = " + isCorrectPassword);
            }
            else if(Objects.equals(path, "/user/userList") && Objects.equals(method,"GET")){
                String requestCookie;
                while (true) {
                    final String line = br.readLine();
                    if (line.isEmpty()) {
                        break;
                    }
                    // header info
                    if (line.startsWith("Cookie")) {
                        requestCookie = line.split(": ")[1];
//                        System.out.println("requestCookie = " + requestCookie);
                        String cookieValue = IOUtils.getCookieValue(requestCookie, "logined");
                        isLoggedIn = cookieValue != null && cookieValue.equals("true");
                    }
                }

            }

            byte[] body;

            if(Objects.equals(path, "/user/signup")&& Objects.equals(method,"POST")){
                body=new byte[]{};
                response302Header(dos, "/index.html");
                responseEndHeader(dos);
            }
            else if(Objects.equals(path, "/user/login") && Objects.equals(method,"POST")){
                if(isCorrectPassword){
                    body=new byte[]{};
                    response302Header(dos, "/index.html");
                    responseLoginCookieHeader(dos);
                    responseEndHeader(dos);
                }
                else{
                    body=new byte[]{};
                    response302Header(dos,"/user/login_failed.html");
                    responseEndHeader(dos);
                }
            }
            else if(Objects.equals(path, "/user/userList") && Objects.equals(method,"GET")){
                if(isLoggedIn){
                    body= Files.readAllBytes(Paths.get("./webapp/"+"user/list.html"));
                    response200Header(dos, body.length);
                    responseEndHeader(dos);
                }
                else{
                    body=new byte[]{};
                    response302Header(dos, "/index.html");
                    responseEndHeader(dos);
                }
            }
            else if (Objects.equals(path, "/css/style.cs") && Objects.equals(method,"GET")){
                body=Files.readAllBytes(Paths.get("./webapp/css/style.css"));
                responseCssHeader(dos, body.length);
                responseEndHeader(dos);
            }
            else{
                body= Files.readAllBytes(Paths.get("./webapp/"+ path));
                response200Header(dos, body.length);
                responseEndHeader(dos);
            }
//            System.out.println("dos = " + dos);
//            System.out.println("body = " + body);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void responseCssHeader(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \rz\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \rz\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseLoginCookieHeader(DataOutputStream dos) {
        try{
            dos.writeBytes("Set-Cookie: logined=true\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void responseEndHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \rz\n");
            dos.writeBytes("Location: " + path + "\r\n");


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