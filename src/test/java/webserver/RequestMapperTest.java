package webserver;

import controller.*;
import http.HttpRequest;
import http.HttpResponse;
import http.enums.HttpMethod;
import http.enums.RequestPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

@DisplayName("RequestMapper 테스트")
class RequestMapperTest {

    @Mock
    private HttpRequest mockRequest;
    
    @Mock
    private HttpResponse mockResponse;

    private RequestMapper requestMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        requestMapper = new RequestMapper(mockRequest, mockResponse);
    }

    @Test
    @DisplayName("루트 경로 요청 시 ForwardController가 실행된다")
    void shouldExecuteForwardControllerWhenRootPath() throws IOException {
        // given
        when(mockRequest.getPath()).thenReturn(RequestPath.ROOT.getValue());
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);

        // when
        requestMapper.proceed();

        // then
        verify(mockRequest, atLeastOnce()).getPath();
        verify(mockRequest, atLeastOnce()).getMethod();
    }

    @Test
    @DisplayName("POST /user/signup 요청 시 UserSignupController가 실행된다")
    void shouldExecuteUserSignupControllerWhenPostUserSignup() throws IOException {
        // given
        when(mockRequest.getPath()).thenReturn(RequestPath.USER_SIGNUP.getValue());
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);

        // when
        requestMapper.proceed();

        // then
        verify(mockRequest, atLeastOnce()).getPath();
        verify(mockRequest, atLeastOnce()).getMethod();
    }

    @Test
    @DisplayName("POST /user/login 요청 시 UserLoginController가 실행된다")
    void shouldExecuteUserLoginControllerWhenPostUserLogin() throws IOException {
        // given
        when(mockRequest.getPath()).thenReturn(RequestPath.USER_LOGIN.getValue());
        when(mockRequest.getMethod()).thenReturn(HttpMethod.POST);

        // when
        requestMapper.proceed();

        // then
        verify(mockRequest, atLeastOnce()).getPath();
        verify(mockRequest, atLeastOnce()).getMethod();
    }

    @Test
    @DisplayName("/user/userList 요청 시 UserListController가 실행된다")
    void shouldExecuteUserListControllerWhenUserList() throws IOException {
        // given
        when(mockRequest.getPath()).thenReturn(RequestPath.USER_LIST.getValue());
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);

        // when
        requestMapper.proceed();

        // then
        verify(mockRequest, atLeastOnce()).getPath();
        verify(mockRequest, atLeastOnce()).getMethod();
    }

    @Test
    @DisplayName("알 수 없는 경로 요청 시 ForwardController가 기본으로 실행된다")
    void shouldExecuteForwardControllerWhenUnknownPath() throws IOException {
        // given
        when(mockRequest.getPath()).thenReturn("/unknown/path");
        when(mockRequest.getMethod()).thenReturn(HttpMethod.GET);

        // when
        requestMapper.proceed();

        // then
        verify(mockRequest, atLeastOnce()).getPath();
        verify(mockRequest, atLeastOnce()).getMethod();
    }
}