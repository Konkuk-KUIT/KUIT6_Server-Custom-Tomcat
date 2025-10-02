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

            String requestLine = br.readLine();
            log.info("Request Line: " + requestLine);
            if (requestLine == null || requestLine.isEmpty()) return;

            String[] tokens = requestLine.split(" ");
            String method = tokens[0];
            String path = tokens[1];

            if (path.equals("/") || path.equals("/index.html")) {
                File file = new File("webapp/index.html");
                int fileLength = (int) file.length();
                response200Header(dos, fileLength);
                responseBody(dos, Files.readAllBytes(Paths.get(file.getPath())));
            }

            if (path.equals("/qna/show.html")) {
                File file = new File("webapp/qna/show.html");
                int fileLength = (int) file.length();
                response200Header(dos, fileLength);
                responseBody(dos, Files.readAllBytes(Paths.get(file.getPath())));
            }

            if (path.equals("/user/form.html")) {
                File file = new File("webapp/user/form.html");
                int fileLength = (int) file.length();
                response200Header(dos, fileLength);
                responseBody(dos, Files.readAllBytes(Paths.get(file.getPath())));
            }

            if (path.contains("/user/signup")) {

                MemoryUserRepository db = MemoryUserRepository.getInstance();
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
                Map<String, String> userInfos = HttpRequestUtils
                        .parseQueryParameter(IOUtils.readData(br, requestContentLength));
                db.addUser(new User(userInfos.get("userId")
                        , userInfos.get("password"), userInfos.get("name"), userInfos.get("email")));
                log.info("가입완료" + userInfos.get("userId"));
                log.info("userId: " + db.findUserById(userInfos.get("userId")).getUserId());
                response302Header(dos, "/index.html");

            }

            if (path.equals("/user/login.html")) {
                File file = new File("webapp/user/login.html");
                int fileLength = (int) file.length();
                response200Header(dos, fileLength);
                responseBody(dos, Files.readAllBytes(Paths.get(file.getPath())));
            }

            if (path.equals("/user/login")) {
                int requestContentLength = 0;
                MemoryUserRepository db = MemoryUserRepository.getInstance();
                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }
                    if (line.startsWith("Content-Length")) {
                        requestContentLength = Integer.parseInt(line.split(": ")[1]);
                    }
                }
                Map<String, String> userInfos = HttpRequestUtils
                        .parseQueryParameter(IOUtils.readData(br, requestContentLength));
                User user = db.findUserById(userInfos.get("userId"));
                if (user == null) {
                    response302Header(dos, "/user/login_failed.html");
                    return;
                }
                if (user.getPassword().equals(userInfos.get("password"))) {
                    responseLoginHeader(dos, "/index.html");
                } else response302Header(dos, "/user/login_failed.html");
            }

            if (path.equals("/user/login_failed.html")) {
                File file = new File("webapp/user/login_failed.html");
                int fileLength = (int) file.length();
                responseLoginFailedHeader(dos, fileLength);
                responseBody(dos, Files.readAllBytes(Paths.get(file.getPath())));
            }

            if(path.equals("/user/userList")){
                String logined = "";
                while (true) {
                    final String line = br.readLine();
                    if (line.equals("")) {
                        break;
                    }
                    if (line.startsWith("Cookie: ")) {
                        String cookieHeader = line.substring("Cookie:".length()).trim();
                        String[] pairs = cookieHeader.split(";");
                        for (String pair : pairs) {
                            String[] kv = pair.trim().split("=", 2); // 최대 2개만 split
                            if (kv.length == 2) {
                                String name = kv[0].trim();
                                String value = kv[1].trim();
                                if (name.equals("logined")) {
                                    logined = value;
                                }
                            }
                        }
                    }
                }
                if(logined.equals("true")){
                    File file = new File("webapp/user/list.html");
                    int fileLength = (int) file.length();
                    response200Header(dos, fileLength);
                    responseBody(dos, Files.readAllBytes(Paths.get(file.getPath())));
                }
            }

            if (path.endsWith(".css")) {
                File file = new File("webapp" + path);
                int fileLength = (int) file.length();
                responseCssHeader(dos, fileLength);
                responseBody(dos, Files.readAllBytes(Paths.get(file.getPath())));
            }

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseCssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseLoginFailedHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: logined=; Path=/; Max-Age=0; HttpOnly; SameSite=Lax\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseLoginHeader(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Set-Cookie: logined=true; Path=/; HttpOnly; SameSite=Lax\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + path);
            dos.writeBytes("\r\n");
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}