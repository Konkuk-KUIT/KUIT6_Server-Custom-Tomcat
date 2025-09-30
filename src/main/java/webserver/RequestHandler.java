package webserver;

import java.io.*;
import java.net.Socket;
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
            String filePath;

            System.out.println(reqHeaderFirstLine);

            switch (tokens[1]) {
                case "/qna/form.html":
                    filePath = "webapp/qna/form.html";
                    break;
                case "/qna/show.html":
                    filePath = "webapp/qna/show.html";
                    break;
                case "/user/form.html":
                    filePath = "webapp/user/form.html";
                    break;
                case "/user/list.html":
                    filePath = "webapp/user/list.html";
                    break;
                case "/user/login.html":
                    filePath = "webapp/user/login.html";
                    break;
                case "/user/login_failed.html":
                    filePath = "webapp/user/login_failed.html";
                    break;
                default:
                    filePath = "webapp/index.html";
                    break;
            }
            System.out.println(filePath);
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