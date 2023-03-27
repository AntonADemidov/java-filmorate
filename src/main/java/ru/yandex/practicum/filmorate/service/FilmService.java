package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;


import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmService(FilmDbStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws Throwable {
        Film newFilm = filmStorage.createFilm(film);
        logger.info(String.format("Новый фильм добавлен в базу: %s c id # %d.", film.getName(), film.getId()));
        return newFilm;
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws Throwable {
        Film newFilm = filmStorage.updateFilm(film);
        logger.info(String.format("Фильм с id # %d обновлен в базе: %s", film.getId(), film.getName()));
        return newFilm;
    }

    @GetMapping
    public Film getFilmById(long filmId) throws DataNotFoundException {
        Film film = filmStorage.getFilmById(filmId);
        logger.info("Найден фильм: {} {}", film.getId(), film.getName());
        return film;
    }

    @PutMapping
    public void addLike(long filmId, long userId) throws DataNotFoundException, DataAlreadyExistException {
        filmStorage.addLike(filmId, userId);
    }

    @GetMapping
    public List<Film> getPopular(long count) {
        return filmStorage.getPopular(count);
    }

    @DeleteMapping
    public void removeLike(long filmId, long userId) throws DataNotFoundException, DataAlreadyExistException {
        filmStorage.removeLike(filmId, userId);
    }
}