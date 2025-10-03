package webserver.controller;

import model.User;
import db.Repository;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.enums.HttpMethod;
import webserver.enums.UserQueryKey;

import java.util.Map;

public class UserController implements Controller {
    private final Repository repo;
    private final String webRoot;
    public UserController(Repository repo, String webRoot){
        this.repo = repo; this.webRoot = webRoot;
    }

    @Override public void service(HttpRequest req, HttpResponse res) throws Exception {
        String p = req.path();
        switch (p) {
            case "/user/signup":
                if (req.method()== HttpMethod.GET) {
                    Map<String,String> q = req.queryParams();
                    signup(q);
                    throw new RedirectSignal("/index.html", null);
                } else if (req.method()==HttpMethod.POST) {
                    Map<String,String> f = req.formParams();
                    signup(f);
                    throw new RedirectSignal("/index.html", null);
                }
                break;

            case "/user/login":
                if (req.method()==HttpMethod.POST) {
                    Map<String,String> f = req.formParams();
                    boolean ok = login(f);
                    if (ok) throw new RedirectSignal("/index.html","logined=true; Path=/; HttpOnly");
                    throw new RedirectSignal("/login_failed.html", null);
                }
                // GET 로그인 지원이 필요하면 여기에 추가
                break;

            case "/user/list":
            case "/user/userList":
                String login = req.cookies().get("logined");
                if (!"true".equalsIgnoreCase(login)) {
                    throw new RedirectSignal("/login.html", null);
                }
                throw new ForwardSignal(webRoot, "/user/list.html");
        }
        // 기본: 정적 파일로 위임
        throw new ForwardSignal(webRoot, p);
    }

    private void signup(Map<String,String> m){
        String id = m.get(UserQueryKey.userId.name());
        String pw = m.get(UserQueryKey.password.name());
        if (id!=null && pw!=null) {
            String name = m.get(UserQueryKey.name.name());
            String email= m.get(UserQueryKey.email.name());
            repo.addUser(new User(id, pw, name, email));
        }
    }
    private boolean login(Map<String,String> m){
        String id = m.get(UserQueryKey.userId.name());
        String pw = m.get(UserQueryKey.password.name());
        if (id==null || pw==null) return false;
        User u = repo.findUserById(id);
        return (u!=null && pw.equals(u.getPassword()));
    }

    // 시그널
    public static class RedirectSignal extends RuntimeException {
        public final String location; public final String cookie;
        public RedirectSignal(String loc, String cookie){ this.location=loc; this.cookie=cookie; }
    }
    public static class ForwardSignal extends RuntimeException {
        public final String webRoot; public final String path;
        public ForwardSignal(String root, String p){ this.webRoot=root; this.path=p; }
    }
}