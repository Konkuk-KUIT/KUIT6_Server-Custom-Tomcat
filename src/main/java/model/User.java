package model;

import java.util.Map;
import java.util.Objects;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;

    private User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static User factory(Map<String, String> userInfoMap) {
         return new User(userInfoMap.get(QueryKey.userId.name()), userInfoMap.get(QueryKey.password.name()), userInfoMap.get(QueryKey.name.name()),  userInfoMap.get(QueryKey.email.name()));
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean isLogin(String id, String password) {
        return this.userId.equals(id) && this.password.equals(password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(getUserId(), user.getUserId()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getName(), user.getName()) && Objects.equals(getEmail(), user.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId(), getPassword(), getName(), getEmail());
    }
}