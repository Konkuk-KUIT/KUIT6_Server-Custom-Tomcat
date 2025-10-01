package webserver.enums;

public enum URI {
    HOME("/"),
    LOGIN("/user/login"),
    SIGNUP("/user/signup"),
    USER_LIST("/user/userList");

    private final String value;

    URI(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
