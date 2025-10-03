package main.java.http.controller;


import main.java.http.enums.HttpMethod;
import java.util.Objects;

public class Route {
    public final HttpMethod method;
    public final String path; // 정규화된 고정 경로 (패턴 매칭은 이후 확장)

    public Route(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route r = (Route) o;
        return method == r.method && Objects.equals(path, r.path);
    }
    @Override public int hashCode() { return Objects.hash(method, path); }
}
