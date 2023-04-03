package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface DirectorDao {

    void createDirector(Director director) throws ValidationException;

    Director getLastAddedDirector();

    void updateDirector(Director director) throws ValidationException;

    void deleteDirectorById(long id);

    Collection<Director> getAllDirectors();

    Director getDirectorById(long id);

    List<Director> getFilmDirectors(long filmId);

    void deleteFilmDirectors(long filmId);

    void addFilmDirectors(Film film);

    void deleteAll();
}