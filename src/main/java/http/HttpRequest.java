package http;

import http.constants.HttpHeader;
import http.constants.HttpMethod;
import http.util.IOUtils;
import http.util.MimeTypes;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.util.logging.Logger;

public class HttpRequest {
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private HttpMethod httpMethod;
    private String url;
    private String httpVersion;
    private String MimeType;
    private String httpBody;

    private HttpRequest(HttpMethod httpMethod, String url, String httpVersion, String mimeType, String body) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.httpVersion = httpVersion;
        this.MimeType = mimeType;
        this.httpBody = body;
    }

    public static HttpRequest from(BufferedReader br) {
        try {
            String request = br.readLine();
            log.info("Request: " + request);

            /** get header */
            String[] tokens = request.split(" ");
            HttpMethod method = HttpMethod.from(tokens[0]);
            String url = tokens[1];
            String httpVersion = tokens[2];
            String mimeType = MimeTypes.fromFilename(url);



            /** get body */
            int contentLength = 0;
            while (true) {
                final String line = br.readLine();
                if (line.equals("")) {
                    break;
                }
                // header info
                if (line.startsWith("Content-Length")) {
                    contentLength = Integer.parseInt(line.split(": ")[1]);
                }
            }
            String body = IOUtils.readData(br, contentLength);

            HttpRequest httpRequest = new HttpRequest(method, url, httpVersion, mimeType, body);
            return httpRequest;


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getUrl() {
        return url;
    }

    public String getHttpBody() {
        return httpBody;
    }

    public String getMimeType() {
        return MimeType;
    }
}
