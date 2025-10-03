package model;

public enum UserField {
    USER_ID("userId"),
    PASSWORD("password"),
    NAME("name"),
    EMAIL("email");

    private final String fieldName;

    UserField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return fieldName;
    }
}