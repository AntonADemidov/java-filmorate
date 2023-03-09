package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private int idCounter = 0;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User getUserById(long userId) throws DataNotFoundException {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new DataNotFoundException("Пользователь с указанным id отсутствует в базе.");
        }
        return userStorage.getUserById(userId);
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        validateUser(user);
        user.setId(++idCounter);
        userStorage.createUser(user);
        logger.info(String.format("Новый пользователь добавлен в базу: %s c id # %d.",user.getName(), user.getId()));
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException, DataNotFoundException {
        if (!userStorage.getUsers().containsKey(user.getId())) {
            throw new DataNotFoundException("Пользователь с указанным id отсутствует в базе.");
        }
        validateUser(user);
        userStorage.createUser(user);
        logger.info(String.format("Пользователь с id# %d обновлен в базе: %s.", user.getId(), user.getName()));
        return user;
    }

    private void validateUser(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @.");
        }

        if (user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы.");
        }

        if (!user.getBirthday().isBefore(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public void addFriend(long userId, long friendId) throws DataNotFoundException {
        validateFriends(userId, friendId);
        final User user = userStorage.getUsers().get(userId);
        final User friend = userStorage.getUsers().get(friendId);
        user.getFriendIds().add(friendId);
        friend.getFriendIds().add(userId);
    }

    public void removeFriend(long userId, long friendId) throws DataNotFoundException {
        validateFriends(userId, friendId);
        final User user = userStorage.getUsers().get(userId);
        final User friend = userStorage.getUsers().get(friendId);
        user.getFriendIds().remove(friendId);
        friend.getFriendIds().remove(userId);
    }

    public List<User> getFriends(long userId) {
        final User user = userStorage.getUsers().get(userId);
        List<User> friends = new ArrayList<>();

        for (long data : user.getFriendIds()) {
            friends.add(userStorage.getUsers().get(data));
        }
        return friends;
    }

    private void validateFriends(long userId, long friendId) throws DataNotFoundException  {
        if (!userStorage.getUsers().containsKey(userId)) {
            throw new DataNotFoundException(String.format("Пользователь с id # %d отсутствует в базе.", userId));
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            throw new DataNotFoundException(String.format("Пользователь с id # %d отсутствует в базе.", userId));
        }
    }

    public List<User> getCommonFriends(long userId, long otherUserId) throws DataNotFoundException {
        validateFriends(userId, otherUserId);
        final Map<Long, User> users = userStorage.getUsers();
        final User user = users.get(userId);
        final User otherUser = users.get(otherUserId);
        final List<User> userFriends = new ArrayList<>();
        final List<User> otherUserFriends = new ArrayList<>();
        final List<User> commonFriends = new ArrayList<>();

        for (long data : user.getFriendIds()) {
            userFriends.add(users.get(data));
        }
        for (long data : otherUser.getFriendIds()) {
            otherUserFriends.add(users.get(data));
        }
        for (User data : userFriends) {
            if (otherUserFriends.contains(data)) {
                commonFriends.add(data);
            }
        }
        return commonFriends;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }
}