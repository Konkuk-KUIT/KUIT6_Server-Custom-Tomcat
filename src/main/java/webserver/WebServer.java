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
        ExecutorService service = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
        }

        // TCP 환영 소켓
        try (ServerSocket welcomeSocket = new ServerSocket(port)){ //port번호에 해당하는 포트에서 들어오는 연결을 받음

            // 연결 소켓
            Socket connection;
            while ((connection = welcomeSocket.accept()) != null) { //클라이언트가 접속을 시도하면, 연결 소켓에 연결함
                // 스레드에 작업 전달
                service.submit(new RequestHandler(connection)); //이후 스레드 풀에 넘겨서 비동기로 실행
            }
        }

    }
}