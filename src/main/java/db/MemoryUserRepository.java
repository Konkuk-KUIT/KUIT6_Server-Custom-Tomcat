package main.java.db;

import main.java.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserRepository implements Repository{
    private final Map<String, User> users = new HashMap<>();
    private static MemoryUserRepository memoryUserRepository;

    private MemoryUserRepository() {// 회원가입 시 url에 생기는 쿼리스트링을 받아오기
    }

    public static MemoryUserRepository getInstance() {
        if (memoryUserRepository == null) {
            memoryUserRepository = new MemoryUserRepository();
            return memoryUserRepository;
        }
        return memoryUserRepository;
    }

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User findUserById(String userId) {
        return users.get(userId);
    }

    public Collection<User> findAll() {
        return users.values();
    }
}