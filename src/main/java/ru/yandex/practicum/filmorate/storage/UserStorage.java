package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface UserStorage {

    User createUser(User user) throws Exception;

    User updateUser(User user) throws Exception;

    Collection<User> findAllUsers();

    User getUserById(long userId);

    Map<Long, User> getUsers();

    void addFriend(long userId, long friendId) throws DataAlreadyExistException;

    List<User> getFriends(long id);

    List<User> getCommonFriends(long userId, long otherUserId);

    void removeFriend(long userId, long friendId) throws DataAlreadyExistException;

    void deleteUser(long id);

    void deleteAll();

}