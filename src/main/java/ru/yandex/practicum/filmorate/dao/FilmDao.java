package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FilmDao {

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> findAllFilms();

    Film getFilmById(long id);

    Map<Long, Film> getFilms();

    void addLike(long filmId, long userId) throws DataAlreadyExistException;

    void removeLike(long filmId, long userId) throws DataAlreadyExistException;

    List<Film> getPopular(long count);
}