package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static enumclasses.HttpHeader.*;
import static enumclasses.HttpHeader.CONTENT_LENGTH;
import static enumclasses.HttpHeader.CONTENT_TYPE;
import static enumclasses.HttpHeader.LOCATION;
import static enumclasses.StatusCode.FOUND;
import static enumclasses.StatusCode.OK;
import static enumclasses.URL.LOGIN;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(HttpResponse.class.getName());
    private final DataOutputStream dos;
    private Map<String, String> header = new HashMap<>();

    private HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public static HttpResponse from(OutputStream out) {
        DataOutputStream dos = new DataOutputStream(out);
        return new HttpResponse(dos);
    }

    public void forward(String path) {
        try {
            responseResource(dos, path);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public void redirect(String path) {
        if(path.equals(LOGIN.URL))
            response302LoginSuccessHeader(dos);
        else
            response302Header(dos, path);
    }

    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes(FOUND.line()+" \r\n");
            dos.writeBytes(SET_COOKIE.text+": logined=true \r\n");
            dos.writeBytes(LOCATION.text+": /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private String contentTypeFor(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".css"))
            return "text/css";
        if (lower.endsWith(".js"))
            return "application/javascript";
        return "text/html;charset=utf-8";
    }

    private void responseResource(DataOutputStream dos, String path) throws IOException {
            byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
            String contentType = contentTypeFor(path);
            response200Header(dos, body.length, contentType);
            responseBody(dos, body);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
        try {
            dos.writeBytes(OK.line()+" \r\n");
            dos.writeBytes(CONTENT_TYPE.text+": "+contentType+"\r\n");
            dos.writeBytes(CONTENT_LENGTH.text +": " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String request) {
        try {
            dos.writeBytes(FOUND.line()+" \r\n");
            dos.writeBytes(LOCATION.text+": " + request + " \r\n");
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
