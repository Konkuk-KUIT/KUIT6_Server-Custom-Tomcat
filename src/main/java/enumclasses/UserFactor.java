package enumclasses;

public enum UserFactor {
    USERID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");
    public final String key;
    UserFactor(String k) {
        this.key = k;
    }
}
