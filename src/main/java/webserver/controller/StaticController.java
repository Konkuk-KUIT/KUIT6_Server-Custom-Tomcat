package webserver.controller;

import webserver.HttpRequest;
import webserver.HttpResponse;

public class StaticController implements Controller {
    private final String webRoot;
    private final String fixedPathOrNull; // null이면 요청 path 사용

    public StaticController(String webRoot) { this(webRoot, null); }
    public StaticController(String webRoot, String fixedPath) {
        this.webRoot = webRoot; this.fixedPathOrNull = fixedPath;
    }

    @Override public void service(HttpRequest req, HttpResponse res) throws Exception {
        String path = (fixedPathOrNull != null) ? fixedPathOrNull : req.path();
        // forward는 RequestHandler에서 OutputStream 넘겨 호출
        throw new ForwardSignal(webRoot, path); // forward 요청 신호
    }

    // forward를 알리기 위한 간단한 시그널 예외
    public static class ForwardSignal extends RuntimeException {
        public final String webRoot; public final String path;
        public ForwardSignal(String root, String p){ this.webRoot=root; this.path=p; }
    }
}