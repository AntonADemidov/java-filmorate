package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmService filmService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserService(UserDbStorage userDbStorage, FilmService filmService) {
        this.userStorage = userDbStorage;
        this.filmService = filmService;
    }

    @PostMapping
    public User createUser(@RequestBody User user) throws Exception {
        User newUser = userStorage.createUser(user);
        logger.info(String.format("Новый пользователь добавлен в базу: %s c id # %d.", user.getName(), user.getId()));
        return newUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws Exception {
        User newUser = userStorage.updateUser(user);
        logger.info(String.format("Пользователь с id# %d обновлен в базе: %s.", user.getId(), user.getName()));
        return newUser;
    }

    @GetMapping
    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @GetMapping
    public User getUserById(long userId) throws DataNotFoundException {
        return userStorage.getUserById(userId);
    }

    @GetMapping
    public List<User> getCommonFriends(long userId, long otherUserId) throws DataNotFoundException {
        return userStorage.getCommonFriends(userId, otherUserId);
    }

    @PutMapping
    public void addFriend(long userId, long friendId) throws DataAlreadyExistException {
        userStorage.addFriend(userId, friendId);
    }

    @GetMapping
    public List<User> getFriends(long id) {
        return userStorage.getFriends(id);
    }

    @DeleteMapping
    public void removeFriend(long userId, long friendId) throws DataAlreadyExistException {
        userStorage.removeFriend(userId, friendId);
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public void deleteUser(long id) {
        userStorage.deleteUser(id);
    }

    public List<Film> recommendationsFilms(long userId) {
        return filmService.getRecommendationsFilms(userId);
    }

    public List<Film> GetCommonFilms(long userId, long friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        return filmService.getCommonFilms(userId, friendId);
    }
}