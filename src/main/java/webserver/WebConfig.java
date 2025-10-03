package webserver;

import controller.*;
import http.enums.HttpMethod;
import http.enums.RequestPath;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller 매핑 설정을 담당하는 클래스
 * Spring의 @Configuration과 유사한 역할
 */
public class WebConfig {

    public static Map<String, Controller> configureControllers() {
        Map<String, Controller> controllers = new HashMap<>();

        // 루트 경로
        controllers.put(createKey(RequestPath.ROOT.getValue(), null), new ForwardController());

        // 회원가입
        controllers.put(createKey(RequestPath.USER_SIGNUP.getValue(), HttpMethod.POST), new UserSignupController());

        // 로그인
        controllers.put(createKey(RequestPath.USER_LOGIN.getValue(), HttpMethod.POST), new UserLoginController());

        // 회원 목록
        controllers.put(createKey(RequestPath.USER_LIST.getValue(), null), new UserListController());

        return controllers;
    }

    public static String createKey(String path, HttpMethod method) {
        if (method == null) {
            return path;
        }
        return path + "_" + method.name();
    }
}