package webserver;
import db.MemoryUserRepository;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable {
    private final Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            String url = request.getPath();

            if ("GET".equalsIgnoreCase(request.getMethod()) && url != null && url.startsWith("/user/signup")) {
                log.log(Level.INFO, "GET signup request userId={0}", request.getParameter("userId"));
                User user = new User(
                        request.getParameter("userId"),
                        request.getParameter("password"),
                        request.getParameter("name"),
                        request.getParameter("email"));
                MemoryUserRepository.getInstance().addUser(user);
                log.log(Level.INFO, "User registered via GET userId={0}", user.getUserId());
                response.response302Header("/index.html");
                return;
            }

            if ("POST".equalsIgnoreCase(request.getMethod()) && "/user/signup".equals(url)) {
                log.log(Level.INFO, "POST signup request userId={0}", request.getParameter("userId"));
                User user = new User(
                        request.getParameter("userId"),
                        request.getParameter("password"),
                        request.getParameter("name"),
                        request.getParameter("email"));
                MemoryUserRepository.getInstance().addUser(user);
                log.log(Level.INFO, "User registered via POST userId={0}", user.getUserId());
                response.response302Header("/index.html");
                return;
            }

            if ("POST".equalsIgnoreCase(request.getMethod()) && "/user/login".equals(url)) {
                User user = MemoryUserRepository.getInstance().findUserById(request.getParameter("userId"));
                log.log(Level.INFO, "Login attempt userId={0}", request.getParameter("userId"));
                if (user != null && user.getPassword().equals(request.getParameter("password"))) {
                    response.addHeader("Set-Cookie", "logined=true");
                    log.log(Level.INFO, "Login success userId={0}", user.getUserId());
                    response.response302Header("/index.html");
                } else {
                    log.log(Level.INFO, "Login failed userId={0}", request.getParameter("userId"));
                    response.response302Header("/user/login_failed.html");
                }
                return;
            }

            if (requiresLogin(url)) {
                String logined = request.getCookie("logined");
                if (!"true".equalsIgnoreCase(logined)) {
                    log.log(Level.INFO, "Access denied (not logged in) url={0}", url);
                    response.response302Header("/user/login.html");
                    return;
                }
                log.log(Level.INFO, "Authenticated access url={0}", url);
            }

            String path = (url == null) ? "/" : url.split("\\?")[0];
            if (path == null || path.isEmpty() || "/".equals(path)) {
                path = "index.html";
            } else if (path.startsWith("/")) {
                path = path.substring(1);
            }

            File webRoot = new File("./webapp").getCanonicalFile();
            String resourcePath = path;
            File target = new File(webRoot, resourcePath).getCanonicalFile();

            if (!target.getPath().startsWith(webRoot.getPath())) {
                log.log(Level.INFO, "Blocked path traversal path={0}", path);
                response.send404(path);
                return;
            }
            
            if (!target.exists() || target.isDirectory()) {
                int lastSlash = resourcePath.lastIndexOf('/');
                int lastDot = resourcePath.lastIndexOf('.');
                boolean hasExtension = lastDot > lastSlash;

                if (!hasExtension) {
                    String candidate = resourcePath.endsWith("/") ? resourcePath + "index.html" : resourcePath + ".html";
                    File fallback = new File(webRoot, candidate).getCanonicalFile();
                    if (fallback.getPath().startsWith(webRoot.getPath()) && fallback.exists() && !fallback.isDirectory()) {
                        resourcePath = candidate;
                        target = fallback;
                    }
                }

                if (!target.exists() || target.isDirectory()) {
                    log.log(Level.INFO, "Static resource not found path={0}", resourcePath);
                    response.send404(resourcePath);
                    return;
                }
            }

            log.log(Level.INFO, "Static resource response path={0}", resourcePath);
            response.forward(resourcePath);

        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private boolean requiresLogin(String url) {
        if (url == null) {
            return false;
        }
        return "/user/list.html".equals(url)
                || "/user/userList".equals(url)
                || "/user/userList.html".equals(url);
    }
}
