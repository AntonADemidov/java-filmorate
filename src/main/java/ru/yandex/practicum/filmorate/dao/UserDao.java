package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserDao {

    User createUser(User user);

    User updateUser(User user);

    Collection<User> findAllUsers();

    User getUserById(long id);

    Map<Long, User> getUsers();

    void addFriend(long userId, long friendId) throws DataAlreadyExistException;

    List<User> getFriends(long id);

    void removeFriend(long userId, long friendId) throws DataAlreadyExistException;

    List<User> getCommonFriends(long userId, long otherUserId);

    void deleteUser(long id);

    void deleteAll();
}