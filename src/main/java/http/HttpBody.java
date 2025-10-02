package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpBody {
    private final String content;

    private HttpBody(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public static HttpBody readBody(BufferedReader br, int requestContentLength) throws IOException {
        if(requestContentLength <= 0) return new HttpBody("");
        char[] buf = new char[requestContentLength]; // 요청 바디 길이만큼 맞는 char[] 배열 만들기
        // buf : 읽은 데이터 담을 배열, 0 : buf 쓰기 시작할 위치
        int read = br.read(buf, 0, requestContentLength); // 최대 contentLength만큼 바디 내용 읽어 buf에 저장
        return new HttpBody(new String(buf, 0, read)); // 배열에 들어있는 내용 String으로 변환해서 반환!
    }

    private static Map<String, String> parseFormData(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&"); // &로 쪼개고
        for (String pair : pairs) {
            String[] keyValues = pair.split("="); // 각 항목들을 = 기준으로 키/값 분리
            if (keyValues.length == 2) {
                params.put(keyValues[0], keyValues[1]);
            }
        }
        return params;
    }

    public Map<String, String> getFormData(){
        return parseFormData(content);
    }
}
