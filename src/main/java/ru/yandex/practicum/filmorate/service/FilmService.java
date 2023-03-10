package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private int idCounter = 0;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, UserService userService) {
        this.filmStorage = inMemoryFilmStorage;
        this.userService = userService;
    }

    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film getFilmById(long filmId) throws DataNotFoundException {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new DataNotFoundException("Фильм с указанным id отсутствует в базе.");
        }
        return filmStorage.getFilmById(filmId);
    }

    public Film createFilm(@RequestBody Film film) throws Throwable {
        validateFilm(film);
        film.setId(++idCounter);
        filmStorage.createFilm(film);
        logger.info(String.format("Новый фильм добавлен в базу: %s c id # %d.", film.getName(), film.getId()));
        return film;
    }

    public Film updateFilm(@RequestBody Film film) throws Throwable {
        if (!filmStorage.getFilms().containsKey(film.getId())) {
            throw new DataNotFoundException("Фильм с указанным id отсутствует в базе.");
        }
        validateFilm(film);
        filmStorage.updateFilm(film);
        logger.info(String.format("Фильм с id # %d обновлен в базе: %s", film.getId(), film.getName()));
        return film;
    }

    private void validateFilm(Film film) throws Exception {
        String text = "Параметр должен быть задан (значение не может быть равно null): ";

        if (film.getName() != null) {
            if (film.getName().isBlank()) {
                throw new ValidationException("Необходимо добавить название фильма (параметр name: не может быть пустым).");
            }
        } else {
            throw new Exception(text + "name.");
        }

        if (film.getDescription() != null) {
            if (!(film.getDescription().length() <= 200)) {
                throw new ValidationException("Необходимо добавить описание фильма (параметр description: до 200 символов.");
            }
        } else {
            throw new Exception(text + "description.");
        }

        if (film.getReleaseDate() != null) {
            if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Необходимо добавить дату релиза (параметр releaseDate: не ранее 28 декабря 1895 года.");
            }
        } else {
            throw new Exception(text + "releaseDate.");
        }

        if (film.getDuration() != null) {
            if (film.getDuration() == null || film.getDuration() < 0) {
                throw new ValidationException("Необходимо добавить продолжительность фильма (параметр duration: положительный).");
            }
        } else {
            throw new Exception(text + "duration.");
        }
    }

    public void addLike(long filmId, long userId) throws DataNotFoundException {
        validateFilmAndUser(filmId, userId);
        final Film film = filmStorage.getFilms().get(filmId);
        final User user = userService.getUserStorage().getUsers().get(userId);
        film.getUserLikes().add(userId);
        user.getFilmLikes().add(filmId);
    }

    public void removeLike(long filmId, long userId) throws DataNotFoundException {
        validateFilmAndUser(filmId, userId);
        final Film film = filmStorage.getFilms().get(filmId);
        final User user = userService.getUserStorage().getUsers().get(userId);
        film.getUserLikes().remove(userId);
        user.getFilmLikes().remove(filmId);
    }

    public List<Film> getPopular(long count) {
        return filmStorage.findAllFilms().stream()
                .sorted(Comparator.comparingLong(Film::likes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private void validateFilmAndUser(long filmId, long userId) throws DataNotFoundException {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            throw new DataNotFoundException(String.format("Фильм с id # %d отсутствует в базе.", filmId));
        }

        if (!userService.getUserStorage().getUsers().containsKey(userId)) {
            throw new DataNotFoundException(String.format("Пользователь с id # %d отсутствует в базе.", userId));
        }
    }
}