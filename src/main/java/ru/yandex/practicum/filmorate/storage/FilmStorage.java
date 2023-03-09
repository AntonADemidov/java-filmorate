package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {

    Collection<Film> findAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Map<Long, Film> getFilms();

    Film getFilmById(long filmId);
}