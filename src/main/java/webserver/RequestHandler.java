package webserver;

import db.MemoryUserRepository;
import db.Repository;
import webserver.controller.Controller;
import webserver.controller.StaticController;
import webserver.controller.UserController;
import webserver.enums.HttpStatus;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class RequestHandler implements Runnable {
    private final Socket connection;
    private final Map<String, Controller> controllers = new HashMap<>();
    private final String WEB_ROOT = "webapp";

    public RequestHandler(Socket connection) {
        this.connection = connection;

        Repository repo = MemoryUserRepository.getInstance();

        // URL → Controller 매핑
        controllers.put("/user/signup", new UserController(repo, WEB_ROOT));
        controllers.put("/user/login",  new UserController(repo, WEB_ROOT));
        controllers.put("/user/list",   new UserController(repo, WEB_ROOT));
        controllers.put("/user/userList", new UserController(repo, WEB_ROOT));

        // 나머지 정적(폴더 프리픽스별로 세분도 가능)
        controllers.put("/", new StaticController(WEB_ROOT)); // index.html용
        // 별도 등록이 없으면 최종적으로 정적 처리로 흘리게 fallback할 예정
    }

    @Override
    public void run() {
        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(in))) {

            HttpRequest req = HttpRequest.from(br);
            HttpResponse res = new HttpResponse();

            // 매핑
            Controller c = controllers.get(req.path());
            if (c == null) {
                // prefix 기반으로 정적 처리: /qna/..., /user/form.html, /css/...
                c = (r, rr) -> { throw new UserController.ForwardSignal(WEB_ROOT, r.path()); };
            }

            try {
                c.service(req, res);
                // Controller가 forward/redirect를 던지지 않고 직접 쓰고 싶다면 여기서 res.writeText 등 호출 가능
                // 기본은 시그널 방식 사용
                res.writeText(out, HttpStatus.NOT_FOUND, "<h1>404</h1>");
            } catch (UserController.RedirectSignal redir) {
                res.redirect(out, redir.location, redir.cookie);
            } catch (UserController.ForwardSignal fwd) {
                res.forward(out, fwd.webRoot, fwd.path);
            } catch (StaticController.ForwardSignal fwd2) {
                res.forward(out, fwd2.webRoot, fwd2.path);
            }

        } catch (Exception ignore) {
        } finally {
            try { connection.close(); } catch (IOException ignored) {}
        }
    }
}