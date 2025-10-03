package test.java.http;

import main.java.http.HttpRequest;
import main.java.http.enums.HttpMethod;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestTest {

    @Test
    void parse_post_form_urlencoded() throws Exception {
        var br = TestUtil.bufferedReaderFromResource("http/post_signup.txt");
        HttpRequest req = HttpRequest.from(br);

        assertThat(req.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(req.getPath()).isEqualTo("/user/signup");
        assertThat(req.getHeader("Content-Type"))
                .startsWith("application/x-www-form-urlencoded");

        assertThat(req.getParam("userId")).isEqualTo("jw");
        assertThat(req.getParam("password")).isEqualTo("pass");
        assertThat(req.getParam("name")).isEqualTo("jungwoo");
        assertThat(req.getParam("email")).isEqualTo("a@b.com"); // %40 → @

        assertThat(req.getBody()).contains("userId=jw");
    }

    @Test
    void parse_get_with_query() throws Exception {
        var br = TestUtil.bufferedReaderFromResource("http/get_with_query.txt");
        HttpRequest req = HttpRequest.from(br);

        assertThat(req.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(req.getPath()).isEqualTo("/user/list.html");
        assertThat(req.getQuery()).isEqualTo("page=2&size=10");
        assertThat(req.getParam("page")).isEqualTo("2");
        assertThat(req.getParam("size")).isEqualTo("10");
    }

    @Test
    void parse_cookie_header() throws Exception {
        var br = TestUtil.bufferedReaderFromResource("http/cookie_request.txt");
        HttpRequest req = HttpRequest.from(br);

        assertThat(req.getCookie("logined")).isEqualTo("true");
        assertThat(req.getCookie("theme")).isEqualTo("dark");
    }
}
