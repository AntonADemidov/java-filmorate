package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger logger =  LoggerFactory.getLogger(UserController.class);
    private final Map<Integer, User> users = new HashMap<>();
    private String message;
    private int idCounter = 0;

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) throws ValidationException {
        validate(user);
        user.setId(++idCounter);
        users.put(user.getId(), user);

        logger.info(String.format("Новый пользователь добавлен в базу: %s c id # %d.",user.getName(), user.getId()));

        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            message = "Пользователь с указанным id отсутствует в базе.";
            logger.error(message);
            throw new ValidationException(message);
        }
        validate(user);
        users.put(user.getId(), user);

        logger.info(String.format("Пользователь с id# %d обновлен в базе: %s.", user.getId(), user.getName()));
        return user;
    }

    private void validate (User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            message = "Электронная почта не может быть пустой и должна содержать символ @.";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (user.getLogin().isBlank()) {
            message = "Логин не может быть пустым и содержать пробелы.";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (!user.getBirthday().isBefore(LocalDate.now())) {
            message = "Дата рождения не может быть в будущем.";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}