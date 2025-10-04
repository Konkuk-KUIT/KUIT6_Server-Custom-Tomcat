package http;

public enum CookieName {
    LOGINED("logined");
    private final String key;
    CookieName(String key){ this.key = key; }
    public String key(){ return key; }
}
