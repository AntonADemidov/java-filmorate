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

        logger.info(String.format("A new movie was created in the database: %s with id # %d", film.getName(),
                film.getId()));
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException {
        if (!films.containsKey(film.getId())) {
            message = "The movie with this id does not exist";
            logger.error(message);
            throw new ValidationException(message);
        }
        validate(film);
        films.put(film.getId(), film);

        logger.info(String.format("The movie has been updated in the database: %s with id # %d", film.getName(),
                film.getId()));
        return film;
    }

    private void validate (Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            message = "The title of the movie cannot be empty";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (!(film.getDescription().length() <= 200)) {
            message = "The maximum length of the description is 200 characters";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            message = "Release date — no earlier than December 28, 1895";
            logger.error(message);
            throw new ValidationException(message);
        }

        if (film.getDuration() < 0) {
            message = "The duration of the film should be positive";
            logger.error(message);
            throw new ValidationException(message);
        }
    }
}