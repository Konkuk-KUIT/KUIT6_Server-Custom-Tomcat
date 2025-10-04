package webserver;

import enums.HtmlUrls;
import enums.HttpUrls;

import java.io.BufferedReader;
import java.io.IOException;
import static http.util.IOUtils.readData;

public class HttpRequest {
    private BufferedReader br;
    private String method;
    private String path;
    private String version;

    private HttpRequest(BufferedReader br) throws IOException {
        this.br = br;
        divideHttpStartLine();
    }

    public static HttpRequest from (BufferedReader br) throws IOException {
        return new HttpRequest(br);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private void divideHttpStartLine() throws IOException {
        String request = br.readLine();
        String[] requestArray = request.trim().split("\\s+");
        this.method = requestArray[0];
        this.path =  requestArray[1];
        this.version = requestArray[2];
    }

    public String getHttpHeader() throws IOException {
        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Cookie")) {
               return HtmlUrls.USERLIST.getPath();
            }
        }
        return HtmlUrls.LOGIN.getPath();
    }

    public String getHttpBody() throws IOException {
        int requestContentLength = 0;
        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            // header info
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }
        return readData(br, requestContentLength);
    }

    public String getQuery() throws IOException {
        int q = path.indexOf('?');
        if (q < 0) return null;
        String query = path.substring(q + 1);
        path = path.substring(0, q);
        return query;
    }

    @Override
    public boolean equals(Object obj) {
        return this.path.equals(obj.toString());
    }

}
