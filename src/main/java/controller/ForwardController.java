package controller;

import http.HttpRequest;
import http.HttpResponse;
import http.enums.RequestPath;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ForwardController implements Controller {
    private static final Logger log = Logger.getLogger(ForwardController.class.getName());

    @Override
    public void execute(HttpRequest request, HttpResponse response) throws IOException {
        String path = request.getPath();
        
        // 1. 루트 경로 ("/") 처리 - 기본 페이지로 변경
        if (path.equals(RequestPath.ROOT.getValue())) {
            path = RequestPath.INDEX.getValue();
        }

        // 2. 보안 검증 - ../ 과 같은 디렉토리 traversal 공격 방지
        if (path.contains("..")) {
            log.log(Level.WARNING, "Directory traversal attack detected: " + path);
            response.notFound();
            return;
        }

        try {
            // 3. 파일 forward
            response.forward(path);
            log.log(Level.INFO, "Static file served successfully: " + path);

        } catch (IOException fileException) {
            // 4. 파일이 없거나 읽기 실패시 404 에러 응답
            log.log(Level.WARNING, "File not found or read error: " + path);
            response.notFound();
        }
    }
}