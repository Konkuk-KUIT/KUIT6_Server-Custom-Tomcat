package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpHeader {
    private final List<String> lines;
    private final int lengthOfContent;

    public HttpHeader(List<String> lines, int lengthOfContent) {
        this.lines = lines;
        this.lengthOfContent = lengthOfContent;
    }

    public static HttpHeader from(BufferedReader br) throws IOException {
        int requestContentLength = 0;
        List<String> lines = new ArrayList<>();
        while (true) {
            final String line = br.readLine();
            if (line.equals("")) {
                break;
            }
            lines.add(line);
            // header info
            if (line.startsWith("Content-Length")) {
                requestContentLength = Integer.parseInt(line.split(": ")[1]);
            }
        }
        return new HttpHeader(lines, requestContentLength);
    }

    public int getLengthOfContent() {
        return lengthOfContent;
    }
}
