package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {

    Collection<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    Map<Long, User> getUsers();

    User getUserById(long userId);
}