package webserver;

import controller.*;
import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import http.constants.HttpHeader;
import http.constants.HttpStatusCode;
import http.util.IOUtils;
import http.util.MimeTypes;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private Controller controller;
    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    MemoryUserRepository memoryUserRepository = MemoryUserRepository.getInstance();

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = HttpResponse.from(dos);
            String url = httpRequest.getUrl();
            String mimeTypes = httpRequest.getMimeType();


            RequestMapper requestMapper = new RequestMapper(httpRequest,httpResponse, br);
            requestMapper.proceed();


        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}