package main.java.http.enums;

public enum QueryKey {
    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String key;

    QueryKey(String key) { this.key = key; }

    public String getKey() { return key; }
}
