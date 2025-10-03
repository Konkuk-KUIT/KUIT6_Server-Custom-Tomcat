package model;

public enum UserFormKey {
    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String key;

    UserFormKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}