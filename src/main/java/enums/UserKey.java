package enums;

public enum UserKey {
    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String key;

    UserKey(String key) {this.key = key;}

    public String getKey() {
        return key;
    }
}
