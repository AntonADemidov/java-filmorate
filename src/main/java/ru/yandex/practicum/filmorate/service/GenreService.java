package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final FilmStorage filmStorage;
    private static final Logger logger = LoggerFactory.getLogger(MpaService.class);

    @Autowired
    public GenreService(FilmDbStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Collection<Genre> getAllGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return filmStorage.getGenreById(id);
    }
}
