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

    public Film createFilm(@RequestBody Film film) throws ValidationException {
        validateFilm(film);
        film.setId(++idCounter);
        filmStorage.createFilm(film);
        logger.info(String.format("Новый фильм добавлен в базу: %s c id # %d.", film.getName(), film.getId()));
        return film;
    }

    public Film updateFilm(@RequestBody Film film) throws ValidationException, DataNotFoundException {
        if (!filmStorage.getFilms().containsKey(film.getId())) {
            throw new DataNotFoundException("Фильм с указанным id отсутствует в базе.");
        }
        validateFilm(film);
        filmStorage.updateFilm(film);
        logger.info(String.format("Фильм с id # %d обновлен в базе: %s", film.getId(), film.getName()));
        return film;
    }

    private void validateFilm(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        if (!(film.getDescription().length() <= 200)) {
            throw new ValidationException("Максимальная длина описания - 200 символов");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть ранее 28 декабря 1895 года");
        }

        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
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