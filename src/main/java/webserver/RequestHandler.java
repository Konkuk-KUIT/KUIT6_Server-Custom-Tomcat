package webserver;

import http.HttpRequest;
import http.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
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
        try (InputStream in = connection.getInputStream(); // 클 -> 서
             OutputStream out = connection.getOutputStream()) { // 서 -> 클

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            // 요청을 파싱하여 HttpRequest 생성
            HttpResponse httpResponse = new HttpResponse(dos);
            // 응답을 작성할 HttpResponse 생성

            RequestMapper requestMapper = new RequestMapper(httpRequest,httpResponse);
            requestMapper.proceed();

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }
}