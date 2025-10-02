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
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
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

            int contentLength = 0;
            String contentType = null;
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {
                int idx = line.indexOf(':');
                if (idx < 0) continue;
                String name  = line.substring(0, idx).trim().toLowerCase(); // ← 소문자 통일
                String value = line.substring(idx + 1).trim();               // ← 공백 제거(중요)
                if (name.equals("content-length")) {
                    contentLength = Integer.parseInt(value);                 // " 78"도 OK
                } else if (name.equals("content-type")) {
                    contentType = value; // 예: "application/x-www-form-urlencoded; charset=UTF-8"
                }
            }

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
            response200Header(dos, body.length);
            responseBody(dos, body);


        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }


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

    private String urlDecode(String s) {
        return s == null ? null : URLDecoder.decode(s, StandardCharsets.UTF_8);
    }


    private byte[] readExactBytes(InputStream in, int len) throws IOException {
        byte[] buf = new byte[len];
        int off = 0;
        while (off < len) {
            int r = in.read(buf, off, len - off);
            if (r == -1) throw new EOFException("Unexpected end of stream while reading body");
            off += r;
        }
        return buf;
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

    private void response404Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
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
            dos.flush();
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