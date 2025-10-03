package http.util;

public record RequestLine(httpMethod method, String path, String version) {
    public RequestLine(String line) {
        this(parseMethod(line), parsePath(line), parseVersion(line));
    }

    private static httpMethod parseMethod(String line) {
        return httpMethod.valueOf(line.split(" ")[0]);
    }

    private static String parsePath(String line) {
        return line.split(" ")[1];
    }

    private static String parseVersion(String line) {
        return line.split(" ")[2];
    }
}
