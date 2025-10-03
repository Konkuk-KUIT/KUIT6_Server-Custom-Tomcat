package webserver;

import db.MemoryUserRepository;
import model.User;
import http.util.HttpRequestUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private final Socket connection;
    private final MemoryUserRepository repository = MemoryUserRepository.getInstance();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);

            String line = br.readLine();
            if (line == null) return;

            String[] tokens = line.split(" ");
            String method = tokens[0];
            String url = tokens[1];

            int contentLength = 0;
            Map<String, String> cookies = null;

            while (!(line = br.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.substring(line.indexOf(':') + 2));
                }
                if (line.startsWith("Cookie:")) {
                    String cookieString = line.substring(line.indexOf(':') + 2);
                    cookies = HttpRequestUtils.parseCookies(cookieString);
                }
            }

            if ("/user/signup".equals(url) && "POST".equalsIgnoreCase(method)) {
                char[] body = new char[contentLength];
                br.read(body);
                Map<String, String> params = HttpRequestUtils.parseQueryString(new String(body));
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                repository.addUser(user);
                log.info("User created: " + user);
                response302Redirect(dos, "/index.html");
                return;
            }

            if (url.startsWith("/user/signup") && "GET".equalsIgnoreCase(method)) {
                String queryString = url.substring(url.indexOf("?") + 1);
                Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                repository.addUser(user);
                log.info("User created: " + user);
                response302Redirect(dos, "/index.html");
                return;
            }

            if ("/user/login".equals(url) && "POST".equalsIgnoreCase(method)) {
                char[] body = new char[contentLength];
                br.read(body);
                Map<String, String> params = HttpRequestUtils.parseQueryString(new String(body));
                User user = repository.findUserById(params.get("userId"));
                if (user != null && user.getPassword().equals(params.get("password"))) {
                    response302LoginSuccess(dos, "/index.html");
                } else {
                    response302Redirect(dos, "/user/login_failed.html");
                }
                return;
            }

            if ("/user/userList".equals(url)) {
                boolean isLoggedIn = cookies != null && "true".equals(cookies.get("logined"));
                if (!isLoggedIn) {
                    response302Redirect(dos, "/user/login.html");
                    return;
                }
                url = "/user/list.html";
            }


            if ("/".equals(url)) url = "/index.html";

            Path filePath = Paths.get("./webapp" + url);
            if (Files.exists(filePath)) {
                byte[] body = Files.readAllBytes(filePath);
                response200Header(dos, body.length, url);
                responseBody(dos, body);
            } else {
                response404(dos);
            }
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String url) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + getContentType(url) + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Redirect(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302LoginSuccess(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Set-Cookie: logined=true; Path=/\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404(DataOutputStream dos) {
        try {
            byte[] body = "404 Not Found".getBytes();
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            responseBody(dos, body);
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

    private String getContentType(String url) {
        if (url.endsWith(".css")) {
            return "text/css";
        }
        if (url.endsWith(".js")) {
            return "application/javascript";
        }
        return "text/html";
    }
}