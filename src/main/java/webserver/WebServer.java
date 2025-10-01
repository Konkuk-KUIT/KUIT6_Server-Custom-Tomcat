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

        /* arg 주는 법
        * 1. 점 세개(More Actions) 클릭
        * 2. Configuration -> Edit
        * 3. Program Argument에 입력
        * 4. OK 클릭
        * 5. 실행!
        * 끝
        * */

        if (args.length != 0) {
            port = Integer.parseInt(args[0]);
            System.out.println("port: " + port);
        }

        // TCP 환영 소켓 -> 클라이언트의 연결 요청을 기다림
        try (ServerSocket welcomeSocket = new ServerSocket(port)){

            // 연결 소켓 -> 클라이언트와 실제 데이터를 주고받는 소켓
            Socket connection;
            while ((connection = welcomeSocket.accept()) != null) {     // 무한 루프 돌면서 client 연결을 기다림, 클라이언트가 접속할 때마다 새 connection 생성
                // 스레드에 작업 전달
                service.submit(new RequestHandler(connection));         // thread pool에 작업 전달, handler는 클라이언트 요청을 처리하는 코드
            }
        }

    }
}