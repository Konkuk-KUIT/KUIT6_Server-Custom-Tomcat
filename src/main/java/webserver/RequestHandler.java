package webserver;

import controller.*;
import db.MemoryUserRepository;
import db.Repository;
import enums.HtmlUrls;
import enums.HttpUrls;
import enums.UserKey;
import model.User;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static db.MemoryUserRepository.getInstance;
import static http.util.HttpRequestUtils.parseQueryParameter;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    private final Repository repository;

    public RequestHandler(Socket connection) {
        this.connection = connection;
        repository = MemoryUserRepository.getInstance();
    }


    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = HttpRequest.from(br);
            HttpResponse httpResponse = new HttpResponse(dos);

            RequestMapper requestMapper = new RequestMapper(httpRequest,httpResponse);
            requestMapper.proceed();
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

}