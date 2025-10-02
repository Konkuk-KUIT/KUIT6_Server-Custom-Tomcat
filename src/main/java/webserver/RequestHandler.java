package main.java.webserver;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import main.java.db.MemoryUserRepository;
import main.java.model.User;
import java.util.HashMap;
import java.util.Map;



public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            DataOutputStream dos = new DataOutputStream(out);

            String requestLine = br.readLine();
            if(requestLine==null) return;

            String[] tokens = requestLine.split(" ");
            String method  = tokens[0];
            String rawPath  = tokens[1];

            int q = rawPath.indexOf('?'); if (q >= 0) rawPath = rawPath.substring(0, q);
            rawPath = rawPath.replaceAll("/+", "/");
            if (rawPath.length() > 1 && rawPath.endsWith("/")) rawPath = rawPath.substring(0, rawPath.length()-1);
            final String path = rawPath;

            Map<String, String> headers = readHeaders(br);
            int contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));


//            if (method.equals("GET") && path.startsWith("/user/signup")) {
//                String[] parts = path.split("\\?", 2);
//                String query = parts.length > 1 ? parts[1] : "";
//
//                Map<String, String> params = parseQueryString(query); // (아래 유틸 참고)
//
//                User u = new User(
//                        urlDecode(params.get("userId")),
//                        urlDecode(params.get("password")),
//                        urlDecode(params.get("name")),
//                        urlDecode(params.get("email"))
//                );
//
//                MemoryUserRepository.getInstance().addUser(u);
//                response302Header(dos, "/index.html");
//                dos.flush();
//                return;
//            }



            if ("POST".equalsIgnoreCase(method) && "/user/signup".equals(path)) {
                char[] buf = new char[contentLength];
                int off = 0;
                while (off < contentLength) {
                    int r = br.read(buf, off, contentLength - off);
                    if (r == -1) break;
                    off += r;
                }
                String body = new String(buf, 0, off);

                Map<String, String> params = parseQueryString(body);

                User u = new User(
                        urlDecode(params.get("userId")),
                        urlDecode(params.get("password")),
                        urlDecode(params.get("name")),
                        urlDecode(params.get("email"))
                );
                MemoryUserRepository.getInstance().addUser(u);


                response302Header(dos, "/index.html");
                dos.flush();
                return;
            }


            if ("POST".equalsIgnoreCase(method) && "/user/login".equals(rawPath)) {
                handleLogin(br,dos,contentLength);
                return;
            }

            if ("GET".equalsIgnoreCase(method) && "/user/list.html".equals(path)) {
                if (!isLogined(headers)) {
                    response302Header(dos, "/user/login.html"); // 로그인 안 되어 있으면 리다이렉트
                    return;
                }}


            // 정적 파일 처리
            String resourcePath = path;
            if ("/".equals(resourcePath)) resourcePath = "/index.html";
            Path filePath = Paths.get("./webapp" + resourcePath);

            if (!Files.exists(filePath)) {
                byte[] body404 = "404 Not Found".getBytes();
                response404Header(dos,body404.length);
                responseBody(dos, body404);
                return;
            }

            byte[] body = Files.readAllBytes(filePath);
            String contentType = guessContentType(resourcePath);
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        } finally {
            try { connection.close(); } catch (Exception ignore) {}
        }


    }

    private String guessContentType(String path) {
        if (path.endsWith(".html")) return "text/html; charset=utf-8";
        if (path.endsWith(".css"))  return "text/css; charset=utf-8";
        if (path.endsWith(".js"))   return "application/javascript; charset=utf-8";
        if (path.endsWith(".png"))  return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        return "application/octet-stream"; // 기본값
    }


    private boolean isLogined(Map<String, String> headers) {
        String cookie = headers.get("Cookie");
        if (cookie == null) return false;
        for (String part : cookie.split(";")) {
            int eq = part.indexOf('=');
            if (eq < 0) continue;
            String k = part.substring(0, eq).trim();
            String v = part.substring(eq + 1).trim();
            if ("logined".equals(k) && "true".equals(v)) return true;
        }
        return false;
    }

    private Map<String, String> readHeaders(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(':');
            if (idx > 0) {
                String key = line.substring(0, idx).trim();
                String val = line.substring(idx + 1).trim();
                headers.put(key, val);
            }
        }
        return headers;
    }

    private String readBody(BufferedReader br, int contentLength) throws IOException {
        char[] buf = new char[contentLength];
        int off = 0;
        while (off < contentLength) {
            int r = br.read(buf, off, contentLength - off);
            if (r == -1) break;
            off += r;
        }
        return new String(buf, 0, off);
    }



    private Map<String, String> parseQueryString(String qs) {
        Map<String, String> map = new HashMap<>();
        if (qs == null || qs.isEmpty()) return map;
        for (String pair : qs.split("&")) {
            String[] kv = pair.split("=", 2);
            String k = kv[0];
            String v = kv.length > 1 ? kv[1] : "";
            map.put(k, v);
        }
        return map;
    }

    private void handleLogin(BufferedReader br, DataOutputStream dos, int contentLength) {
        try {
            String body = readBody(br, contentLength);
            Map<String, String> form = parseQueryString(body);

            String userId = form.get("userId");
            String password = form.get("password");

            User found = MemoryUserRepository.getInstance().findUserById(userId);

            if (found != null && found.getPassword().equals(password)) {
                response302WithCookie(dos, "/index.html", "logined=true; Path=/");
            } else {
                response302Header(dos, "/user/login_failed.html");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, "login error: " + e.getMessage(), e);
            response302Header(dos, "/user/login_failed.html");
        }
    }

    private String urlDecode(String s) {
        return s == null ? null : URLDecoder.decode(s, StandardCharsets.UTF_8);
    }


    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Connection: close\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("Connection: close\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String path) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + path + "\r\n");
            dos.writeBytes("Connection: close\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302WithCookie(DataOutputStream dos, String location, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found\r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("Connection: close\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) { log.log(Level.SEVERE, e.getMessage()); }
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