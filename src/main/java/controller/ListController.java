//회원 목록 조회 페이지 처리
package controller;

import db.MemoryUserRepository;
import http.request.HttpRequest;
import http.response.HttpResponse;

public class ListController implements Controller {

    @Override
    public void execute(HttpRequest req, HttpResponse resp) throws Exception {
        // 단순히 userList.html 전달 (더 복잡한 동적 렌더링 구현 가능)
        resp.forward("/user/list.html");
    }
}

