package main.java.http.controller;

import main.java.http.enums.HttpMethod;

public final class Routes {
    private Routes() {}

    public static Router create() {
        Router r = new Router();
        r.register(HttpMethod.POST, "/user/signup", new UserSignupController());
        r.register(HttpMethod.POST, "/user/login",  new UserLoginController());
        r.register(HttpMethod.GET,  "/user/list.html", new UserListController());
        r.fallback(new StaticResourceController());
        return r;
    }
}