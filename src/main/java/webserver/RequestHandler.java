package webserver;

import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static db.MemoryUserRepository.getInstance;
import static http.util.HttpRequestUtils.parseQueryParameter;
import static http.util.IOUtils.readData;

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
            String[] requestArray = request.trim().split("\\s+");
            String fileUrl = "/Users/sooa/Documents/kuit/KUIT6_Server-Custom-Tomcat/webapp";
            String uri =  requestArray[1];
            if(uri.equals("/")){
                uri = "/index.html";
            }

            String query = "";
            if(uri.indexOf("?") >= 0){
                query = uri.substring(uri.indexOf('?') + 1);
                uri = uri.substring(0, uri.indexOf('?'));
            }
            if(query != null && !query.isEmpty()){
                Map<String, String> userString = parseQueryParameter(query);
                User user = new User(userString.get("userId"), userString.get("password"), userString.get("name"), userString.get("email"));
                getInstance().addUser(user);
                uri = "/index.html";
            }
            if(uri.equals("/user/signup")){
                Integer requestContentLength = 0;
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
                String requestBody = readData(br, requestContentLength);
                Map<String, String> userString = parseQueryParameter(requestBody);
                User user = new User(userString.get("userId"), userString.get("password"), userString.get("name"), userString.get("email"));
                getInstance().addUser(user);
                uri = "/index.html";
                response302Header(dos, uri, false);
                return;
            }
            if(uri.equals("/user/login")){
                Integer requestContentLength = 0;
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
                String requestBody = readData(br, requestContentLength);
                Map<String, String> userString = parseQueryParameter(requestBody);
                if(getInstance().findUserById(userString.get("userId")) == null){
                    uri = "/user/login_failed.html";
                } else {
                    uri = "/index.html";
                    response302Header(dos, uri, true);
                    return;
                }
            }
            if(uri.equals("/user/userList")){
                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }
                    System.out.println("시도");
                    // header info
                    if (line.startsWith("Cookie")) {
                        uri = "/user/list.html";
                        response302Header(dos, uri, true);
                        break;
                    }
                    System.out.println("ㄱ끝..");
                }
                uri = "/user/login.html";
            }
            byte[] body = Files.readAllBytes(Paths.get(fileUrl+uri));
            response200Header(dos, body.length);
            responseBody(dos, body);
            responseCSS(dos);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void responseCSS(DataOutputStream dos) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Accept: text/css,*/*;q=0.1\r\n");
            dos.writeBytes("/Users/sooa/Documents/kuit/KUIT6_Server-Custom-Tomcat/webapp/css/style.css\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }
    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String bodyContent, boolean isCookie) throws IOException {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location:" + bodyContent +"\r\n");
            if(isCookie) dos.writeBytes("Set-Cookie: logined=true" + "\r\n");
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