package webserver;

import constant.HttpHeader;
import constant.HttpStatus;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpResponse {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());


    public HttpResponse() {
    }

    public static void writeResponse(DataOutputStream dos, HttpStatus status, String contentType, byte[] body) {
        try {
            dos.writeBytes(status.getStatusLine());
            dos.writeBytes(HttpHeader.CONTENT_TYPE.key() + ": " + contentType + "\r\n");
            dos.writeBytes(HttpHeader.CONTENT_LENGTH.key() + ": " + body.length + "\r\n");
            dos.writeBytes("\r\n");
            dos.write(body);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public static void writeRedirect(DataOutputStream dos, String location) {
        try {
            dos.writeBytes(HttpStatus.FOUND.getStatusLine());
            dos.writeBytes(HttpHeader.LOCATION.key() + ": " + location + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    public static void writeRedirectWithCookie(DataOutputStream dos, String location, String cookie) {
        try {
            dos.writeBytes(HttpStatus.FOUND.getStatusLine());
            dos.writeBytes(HttpHeader.LOCATION.key() + ": " + location + "\r\n");
            dos.writeBytes(HttpHeader.SET_COOKIE.key() + ": " + cookie + "\r\n");
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }




}
