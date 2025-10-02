package webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class WebServer {
    private static final int DEFAULT_PORT = 80;
    private static final int DEFAULT_THREAD_NUM = 50;
    private static final Logger log = Logger.getLogger(WebServer.class.getName());

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        //쓰레드 풀로 관리하는 거! 기본 50개를 설정했다고 볼 수 있음
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);

        if (args.length != 0) {//만약 port를 설정한다면 그걸로 하고 아니면 기본 포트 80으로
            port = Integer.parseInt(args[0]);
        }

        // TCP 환영 소켓
        try (ServerSocket welcomeSocket = new ServerSocket(port)){
            // 연결 소켓
            Socket connection;
            while ((connection = welcomeSocket.accept()) != null) {
                // 스레드에 작업 전달
                service.submit(new RequestHandler(connection));
            }
        }

    }
}
