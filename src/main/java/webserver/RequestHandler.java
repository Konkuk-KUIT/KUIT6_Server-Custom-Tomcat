package webserver;

import db.MemoryUserRepository;
import model.User;

import java.io.*;
import java.net.Socket;
import java.net.http.HttpRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            String reqHeaderFirstLine = br.readLine();
            String[] tokens = reqHeaderFirstLine.split(" ");

            if(tokens[0].equals("POST") && tokens[1].equals("/user/signup")) {
                String line;
                int contentLength = 0;
                while(!(line = br.readLine()).isEmpty()) {
                    if(line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }
                char[] bodyData = new char[contentLength];
                br.read(bodyData, 0, contentLength);
                String reqBody = new String(bodyData);
                String[] queryToken = reqBody.split("&");
                MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();
                memoryUserRepository.addUser(new User(queryToken[0].split("=")[1],
                        queryToken[1].split("=")[1],
                        queryToken[2].split("=")[1],
                        queryToken[3].split("=")[1]));

            }

            String filePath = switch (tokens[1]) {
                case "/qna/form.html" -> "webapp/qna/form.html";
                case "/qna/show.html" -> "webapp/qna/show.html";
                case "/user/form.html" -> "webapp/user/form.html";
                case "/user/list.html" -> "webapp/user/list.html";
                case "/user/login.html" -> "webapp/user/login.html";
                case "/user/login_failed.html" -> "webapp/user/login_failed.html";
                default -> "webapp/index.html";
            };

            byte[] body = Files.readAllBytes(Paths.get(filePath));
            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
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