package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    @Override
    public Film getFilmById(long filmId) {
        return films.get(filmId);
    }

    @Override
    public Film createFilm(Film film) {
        return films.put(film.getId(), film);
    }

    @Override
    public Film updateFilm(Film film) {
        return films.put(film.getId(), film);
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }
}