package http;

import java.io.OutputStream;

public class HttpResponse {
    private final OutputStream dos;

    public HttpResponse(OutputStream dos) {
        this.dos = dos;
    }
}
