package webserver.enums;

public enum Path {
    HOME("/"),
    LOGIN("/user/login"),
    SIGNUP("/user/signup"),
    USER_LIST("/user/userList");

    private final String value;

    Path(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
