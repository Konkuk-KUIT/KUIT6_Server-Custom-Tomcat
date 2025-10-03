package http.util;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {
    private BufferedReader br;
    private HttpMethod method;
    private String requestURI;

    public String getRequestURI() {
        return requestURI;
    }

    public HttpMethod getMethod() {
        return method;
    }


    private HttpRequest(BufferedReader br) {
        this.br = br;

        //소켓에서 받은 inputStream 읽어서 파싱하기
        String requestLine = null;
        try {
            requestLine = br.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (requestLine == null || requestLine.isEmpty()) {
            return;
        }

        String[] parts = requestLine.split(" ", 3);
        if (parts.length < 2) {
            return;
        }
        method = HttpMethod.from(parts[0]);
        requestURI = parts[1];

    }

    public String getPostRequest() throws IOException {
        int requestContentLength = 0;
        requestContentLength = getRequestContentLength(br, requestContentLength);
        return IOUtils.readData(br, requestContentLength);
    }

    private static int getRequestContentLength(BufferedReader br, int requestContentLength) throws IOException {
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
        return requestContentLength;
    }
    public static HttpRequest from(BufferedReader br) {


        return new HttpRequest(br);
    }

    public BufferedReader getBr() {
        return br;
    }
}
