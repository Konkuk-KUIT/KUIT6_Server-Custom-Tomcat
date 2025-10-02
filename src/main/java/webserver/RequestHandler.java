package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
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

            if(path.equals("/qna/show.html")){
                File file = new File("webapp/qna/show.html");
                int fileLength = (int) file.length();
                response200Header(dos, fileLength);
                responseBody(dos, Files.readAllBytes(Paths.get(file.getPath())));
            }

            if(path.equals("/user/form.html")){
                File file = new File("webapp/user/form.html");
                int fileLength = (int) file.length();
                response200Header(dos, fileLength);
                responseBody(dos, Files.readAllBytes(Paths.get(file.getPath())));
            }

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