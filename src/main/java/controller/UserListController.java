package controller;

import enumclasses.RedirectTarget;
import webserver.HttpRequest;
import webserver.HttpResponse;

import java.util.logging.Logger;

public class UserListController implements Controller {
    private static final Logger log = Logger.getLogger(UserListController.class.getName());

    @Override
    public void service(HttpRequest request, HttpResponse response) {
        //로그인 상태일 때 userList조회
        if (!request.isLogined()) {
            response.redirect(RedirectTarget.LOGIN.route);
            return;
        }
        response.redirect(RedirectTarget.USERLIST.route);
    }
}
