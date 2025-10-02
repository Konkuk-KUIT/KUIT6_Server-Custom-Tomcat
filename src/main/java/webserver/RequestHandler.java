package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequestUtils;
import http.util.IOUtils;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);

            String line = br.readLine();
            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");
            String method = tokens[0];
            String url = tokens[1];

            if (method.equals("POST") && url.equals("/user/signup")) {
                int contentLength = 0;
                String headerLine = line;
                while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
                    if (headerLine.startsWith("Content-Length")) {
                        contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                    }
                }

                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                MemoryUserRepository.getInstance().addUser(user);
                response302Header(dos, "/index.html");
                return;
            }

            if (method.equals("POST") && url.equals("/user/login")) {
                int contentLength = 0;
                String headerLine = line;
                while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
                    if (headerLine.startsWith("Content-Length")) {
                        contentLength = Integer.parseInt(headerLine.split(":")[1].trim());
                    }
                }

                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryParameter(body);
                User user = MemoryUserRepository.getInstance().findUserById(params.get("userId"));

                if (user != null && user.getPassword().equals(params.get("password"))) {
                    response302HeaderWithCookie(dos, "/index.html");
                } else {
                    response302Header(dos, "/user/login_failed.html");
                }
                return;
            }

            String path = url.split("\\?")[0];
            if (path.equals("/")) {
                path = "/index.html";
            }

            File file = new File("./webapp" + path);
            if (!file.exists()) {
                response404(dos);
                return;
            }

            String contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            byte[] body = Files.readAllBytes(file.toPath());
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true\r\n");
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

    private void response404(DataOutputStream dos) {
        try {
            String notFoundBody = "<h1>404 Not Found</h1>";
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + notFoundBody.getBytes().length + "\r\n");
            dos.writeBytes("\r\n");
            dos.writeBytes(notFoundBody);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
