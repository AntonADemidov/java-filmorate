package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private String message;
    private int idCounter = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
        validate(film);
        film.setId(++idCounter);
        films.put(film.getId(), film);

        logger.info(String.format("Новый фильм добавлен в базу: %s c id # %d.", film.getName(), film.getId()));

        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            message = "Пользователь с указанным id отсутствует в базе.";
            logger.error(message);
            throw new ValidationException(message);
        }
        validate(film);
        films.put(film.getId(), film);

        logger.info(String.format("Фильм с id # %d обновлен в базе: %s", film.getId(), film.getName()));

        return film;
    }

    private void validate (Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            message = "Название фильма не может быть пустым";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (!(film.getDescription().length() <= 200)) {
            message = "Максимальная длина описания - 200 символов";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            message = "Дата релиза не может быть ранее 28 декабря 1895 года";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (film.getDuration() < 0) {
            message = "Продолжительность фильма должна быть положительной";
            logger.error(message);
            throw new ValidationException(message);
        }
    }
}