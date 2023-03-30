package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FeedDao;
import ru.yandex.practicum.filmorate.dao.FeedDaoImpl;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.UserDaoImpl;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;

@Component
public class UserDbStorage implements UserStorage {
    private final UserDao userDao;
    private final FeedDao feedDao;
    private int idCounter = 0;

    public UserDbStorage(UserDaoImpl userDaoImpl, FeedDaoImpl feedDaoImpl) {
        this.userDao = userDaoImpl;
        this.feedDao = feedDaoImpl;
    }

    @Override
    public User createUser(User user) throws Exception {
        validateUser(user);
        user.setId(++idCounter);
        return userDao.createUser(user);
    }

    @Override
    public User updateUser(User user) throws Exception {
        validateUser(user);
        return userDao.updateUser(user);
    }

    @Override
    public Collection<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    @Override
    public User getUserById(long userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Map<Long, User> getUsers() {
        return userDao.getUsers();
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) throws DataNotFoundException {
        validateFriends(userId, otherUserId);
        return userDao.getCommonFriends(userId, otherUserId);
    }

    @Override
    public void addFriend(long userId, long friendId) throws DataAlreadyExistException {
        validateFriends(userId, friendId);
        userDao.addFriend(userId, friendId);
        feedDao.addFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(long id) {
        return userDao.getFriends(id);
    }

    @Override
    public void removeFriend(long userId, long friendId) throws DataAlreadyExistException {
        validateFriends(userId, friendId);
        userDao.removeFriend(userId, friendId);
        feedDao.removeFriend(userId, friendId);
    }

    @Override
    public List<Feed> getFeeds(long userId) {
        return feedDao.getFeeds(userId);
    }

    private void validateUser(User user) throws Exception {
        String text = "Параметр должен быть задан (значение не может быть равно null): ";

        if (user.getEmail() != null) {
            if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                throw new ValidationException("Необходимо добавить электронную почту (параметр email: не может быть пустым" +
                        " и должен содержать символ @).");
            }
        } else {
            throw new Exception(text + "email.");
        }

        if (user.getLogin() != null) {
            if (user.getLogin().isBlank()) {
                throw new ValidationException("Необходимо добавить логин (параметр login: не может быть пустым и содержать пробелы).");
            }
        } else {
            throw new Exception(text + "login.");
        }

        if (user.getBirthday() != null) {
            if (!user.getBirthday().isBefore(LocalDate.now())) {
                throw new ValidationException("Необходимо добавить дату рождения (параметр birthday: не может быть в будущем).");
            }
        } else {
            throw new Exception(text + "birthday.");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void validateFriends(long userId, long friendId) throws DataNotFoundException  {
        if (!getUsers().containsKey(userId)) {
            throw new DataNotFoundException(String.format("Пользователь с id # %d отсутствует в базе.", userId));
        }
        if (!getUsers().containsKey(friendId)) {
            throw new DataNotFoundException(String.format("Пользователь с id # %d отсутствует в базе.", userId));
        }
    }
}