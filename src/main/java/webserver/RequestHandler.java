package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
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
            Map<String, String> paramMap = HttpRequestUtils.parseQueryParams(queryString);
            System.out.println("method = " + method);
            System.out.println("path = " + path);
            System.out.println("paramMap = " + paramMap);

// sign-up 요청: http://localhost/user/signup?userId=userid&password=password&name=name&email=email%40k.com
            if(Objects.equals(path, "/user/signup") && Objects.equals(method,"POST")){
                User user = new User(paramMap.get("userId"),
                        paramMap.get("password"),
                        paramMap.get("name"),
                        paramMap.get("email"));
                MemoryUserRepository.getInstance().addUser(user);
                path="index.html";
            }
            if(Objects.equals(path, "")){
                path="index.html";
            }

            byte[] body;

            if(Objects.equals(path, "/user/signup")){
                body=new byte[]{};
                response302Header(dos, "/index.html");
            }
            else{
                 body= Files.readAllBytes(Paths.get("./webapp/"+ path));
                response200Header(dos, body.length);
            }
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \rz\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \rz\n");
//            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Content-Length: 0\r\n");

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