package http.util;

import db.MemoryUserRepository;

import java.io.BufferedReader;
import java.io.IOException;

public class IOUtils {
    /**
     * @param br
     * socket으로부터 가져온 InputStream
     * @param contentLength
     * 헤더의 Content-Length의 값이 들어와야한다.
     */
    BufferedReader br;
    int contentLength;
    private static IOUtils ioUtils;

    public IOUtils() {}

    public static IOUtils getInstance() {
        if (ioUtils == null) {
            ioUtils = new IOUtils();
            return ioUtils;
        }
        return ioUtils;
    }


//    public IOUtils(BufferedReader br, int contentLength) {
//        this.br = br;
//        this.contentLength = contentLength;
//    }


    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }
}