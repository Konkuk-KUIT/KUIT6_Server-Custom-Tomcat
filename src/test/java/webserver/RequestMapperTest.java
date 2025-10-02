package webserver;

import controller.Controller;
import enums.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class RequestMapperTest {

    @Test
    @DisplayName("POST /user/signup 매핑 확인")
    void testSignUpPostMapping() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = mock(HttpResponse.class);
        Controller controller = mock(Controller.class);

        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getUrl()).thenReturn("/user/signup");

        RequestMapper mapper = new RequestMapper(request, response);
        mapper.controllers.put("POST /user/signup", controller);

        mapper.proceed();

        verify(controller).execute(request, response);
    }

    @Test
    @DisplayName("POST /user/login 매핑 확인")
    void testLoginPostMapping() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = mock(HttpResponse.class);
        Controller controller = mock(Controller.class);

        when(request.getMethod()).thenReturn(HttpMethod.POST);
        when(request.getUrl()).thenReturn("/user/login");

        RequestMapper mapper = new RequestMapper(request, response);
        mapper.controllers.put("POST /user/login", controller);

        mapper.proceed();

        verify(controller).execute(request, response);
    }

    @Test
    @DisplayName("알 수 없는 요청은 defaultController 실행")
    void testUnknownMapping() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = mock(HttpResponse.class);

        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getUrl()).thenReturn("/nope");

        RequestMapper mapper = new RequestMapper(request, response);

        // 실행만 잘 되는지 확인 (defaultController 실행됨)
        mapper.proceed();
    }
}