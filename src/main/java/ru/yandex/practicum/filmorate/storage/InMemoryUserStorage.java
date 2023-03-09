package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(long userId) {
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public User updateUser(@RequestBody User user) {
        return users.put(user.getId(), user);
    }

    @Override
    public Map<Long, User> getUsers() {
        return users;
    }
}