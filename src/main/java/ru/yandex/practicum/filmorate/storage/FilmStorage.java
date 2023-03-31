package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FilmStorage {

    Collection<Film> findAllFilms();

    Film createFilm(Film film) throws Exception;

    Film updateFilm(Film film) throws Exception;

    Map<Long, Film> getFilms();

    Film getFilmById(long filmId);

    Collection<Mpa> getAllMpa();

    Mpa getMpaById(int id);

    void addLike(long filmId, long userId) throws DataAlreadyExistException;

    List<Film> getPopular(long count);

    void removeLike(long filmId, long userId) throws DataAlreadyExistException;

    Collection<Genre> getAllGenres();

    Genre getGenreById(int id);

    Collection<Film> getDirectorFilmsOrderByLikes(long directorId);

    Collection<Film> getDirectorFilmsOrderByYear(long directorId);

    void deleteFilm(long id);

    void deleteAll();

    List<Film> getRecommendationsFilms(long userId);

    List<Film> searchFilm(String query, String by) throws ValidationException;

    List<Film> getAllPopularFilmsOrderByLikes(long count, Integer genreId, Integer year);
}