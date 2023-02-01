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

        logger.info(String.format("A new user has been added to the database: %s with id # %d",user.getName(),
                user.getId()));
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException {
        if (!users.containsKey(user.getId())) {
            message = "The user with this id does not exist";
            logger.error(message);
            throw new ValidationException(message);
        }
        validate(user);
        users.put(user.getId(), user);

        logger.info(String.format("The user has been updated in the database: %s with id # %d", user.getName(),
                user.getId()));
        return user;
    }

    private void validate (User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            message = "The email cannot be empty and must contain the character @";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (user.getLogin().isBlank()) {
            message = "The login cannot be empty and contain spaces";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (!user.getBirthday().isBefore(LocalDate.now())) {
            message = "The date of birth cannot be in the future";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}