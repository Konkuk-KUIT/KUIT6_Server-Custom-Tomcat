package webserver;

import db.MemoryUserRepository;
import model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RequestHandlerTest {

    private Socket mockSocket(String httpRequest, ByteArrayOutputStream output) throws Exception {
        Socket socket = mock(Socket.class);
        when(socket.getInputStream()).thenReturn(new ByteArrayInputStream(httpRequest.getBytes()));
        when(socket.getOutputStream()).thenReturn(output);
        return socket;
    }

    @Test
    void handleGetRootRequest() throws Exception {
        String httpRequest = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        RequestHandler handler = new RequestHandler(mockSocket(httpRequest, output));

        handler.run();

        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 200 OK");
        assertThat(response).contains("<html");
    }

    @Test
    @DisplayName("GET /user/signup 요청 시 회원가입 폼 페이지 보여줌")
    void handleGetSignupRequest() throws Exception {
        String request = "GET /user/signup HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "\r\n";

        Socket socket = mock(Socket.class);
        ByteArrayInputStream in = new ByteArrayInputStream(request.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        when(socket.getInputStream()).thenReturn(in);
        when(socket.getOutputStream()).thenReturn(out);

        new RequestHandler(socket).run();

        String response = out.toString();
        assertThat(response).contains("HTTP/1.1 200 OK");
        assertThat(response).contains("<html"); // form.html 내용 확인
    }

    @Test
    @DisplayName("POST /user/signup 요청 시 회원가입 처리 후 index.html로 리다이렉트")
    void handlePostSignupRequest() throws Exception {
        String body = "userId=test&password=1234&name=kim&email=test@test.com";
        String httpRequest = "POST /user/signup HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n\r\n" +
                body;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        RequestHandler handler = new RequestHandler(mockSocket(httpRequest, output));
        handler.run();

        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("Location: /index.html");
    }

    @Test
    @DisplayName("POST /user/login 요청 시 로그인 성공하면 쿠키 세팅")
    void handlePostLoginSuccess() throws Exception {
        MemoryUserRepository.getInstance().addUser(new User("test", "1234", "kim", "test@test.com"));

        String body = "userId=test&password=1234";
        String httpRequest = "POST /user/login HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n\r\n" +
                body;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        RequestHandler handler = new RequestHandler(mockSocket(httpRequest, output));
        handler.run();

        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("Set-Cookie: logined=true");
    }

    @Test
    @DisplayName("POST /user/login 요청 시 로그인 실패하면 user/login_failed.html로 리다이렉트")
    void handlePostLoginFail() throws Exception {
        String body = "userId=wrong&password=wrong";
        String httpRequest = "POST /user/login HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "Content-Type: application/x-www-form-urlencoded\r\n\r\n" +
                body;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        RequestHandler handler = new RequestHandler(mockSocket(httpRequest, output));
        handler.run();

        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("/user/login_failed.html");
    }

    @Test
    @DisplayName("GET /user/userList 요청 시 로그인 상태라면 userList.html 반환")
    void handleUserListWhenLogined() throws Exception {
        String httpRequest = "GET /user/userList HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Cookie: logined=true\r\n\r\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        RequestHandler handler = new RequestHandler(mockSocket(httpRequest, output));
        handler.run();

        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 200 OK");
        assertThat(response).contains("<html");
    }

    @Test
    @DisplayName("GET /user/userList 요청 시 비로그인 상태라면 login.html로 리다이렉트")
    void handleUserListWhenNotLogined() throws Exception {
        String httpRequest = "GET /user/userList HTTP/1.1\r\nHost: localhost\r\n\r\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        RequestHandler handler = new RequestHandler(mockSocket(httpRequest, output));
        handler.run();

        String response = output.toString();
        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("/user/login.html");
    }
}