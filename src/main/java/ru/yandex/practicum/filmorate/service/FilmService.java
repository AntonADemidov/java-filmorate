package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FilmService {
    FilmStorage filmStorage;
    DirectorDao directorDao;

    @Autowired
    public FilmService(FilmDbStorage filmStorage, DirectorDao directorDao) {
        this.filmStorage = filmStorage;
        this.directorDao = directorDao;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws Throwable {
        Film newFilm = filmStorage.createFilm(film);
        log.info(String.format("Новый фильм добавлен в базу: %s c id # %d.", film.getName(), film.getId()));
        return newFilm;
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws Throwable {
        Film newFilm = filmStorage.updateFilm(film);
        log.info(String.format("Фильм с id # %d обновлен в базе: %s", film.getId(), film.getName()));
        return newFilm;
    }

    @GetMapping
    public Film getFilmById(long filmId) throws DataNotFoundException {
        Film film = filmStorage.getFilmById(filmId);
        log.info(String.format("Найден фильм: %d %s", film.getId(), film.getName()));
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

    public Collection<Film> getDirectorFilmsOrderByLikes(long directorId) {
        directorDao.getDirectorById(directorId);
        return filmStorage.getDirectorFilmsOrderByLikes(directorId);
    }

    public Collection<Film> getDirectorFilmsOrderByYear(long directorId) {
        directorDao.getDirectorById(directorId);
        return filmStorage.getDirectorFilmsOrderByYear(directorId);
    }

    public void deleteFilm(long id) {
        filmStorage.deleteFilm(id);
    }

    public List<Film> getRecommendationsFilms(long userId) {
        return filmStorage.getRecommendationsFilms(userId);
    }

    @GetMapping
    public List<Film> searchFilm(String query, String by) throws ValidationException {
        return filmStorage.searchFilm(query, by);
    }

    public List<Film> getCommonFilms(long userId, long friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getAllPopularFilmsOrderByLikes(long count, Integer genreId, Integer year) {
        return filmStorage.getAllPopularFilmsOrderByLikes(count, genreId, year);
    }
}