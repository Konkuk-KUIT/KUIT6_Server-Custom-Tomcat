package model;

import java.util.Objects;

public class User {
    private String userId;
    private String password;
    private String name;
    private String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;      // getClass(): 해당 객체가 어떤 클래스인지 나타내는 Class 객체를 반환
        User user = (User) o;
        return Objects.equals(getUserId(), user.getUserId()) && Objects.equals(getPassword(), user.getPassword()) && Objects.equals(getName(), user.getName()) && Objects.equals(getEmail(), user.getEmail());
    }

    @Override       // hashcode: 객체를 식별하기 위한 정수 값 => 객체를 숫자로 변환한 대표값
    public int hashCode() {
        return Objects.hash(getUserId(), getPassword(), getName(), getEmail());
    }

}