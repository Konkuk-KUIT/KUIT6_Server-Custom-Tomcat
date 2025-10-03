package main.java.http.controller;

import java.util.*;
import main.java.http.enums.HttpMethod;

public class Router {
    private final Map<Route, Controller> routes = new HashMap<>();
    private Controller fallback;

    public Router register(HttpMethod method, String path, Controller c) {
        routes.put(new Route(method, path), c);
        return this;
    }
    public Router fallback(Controller c) { this.fallback = c; return this; }

    public Controller resolve(HttpMethod method, String path) {
        Controller c = routes.get(new Route(method, path));
        return (c != null) ? c : fallback;
    }
}
