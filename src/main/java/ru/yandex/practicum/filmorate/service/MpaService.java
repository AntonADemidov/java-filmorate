package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Service
public class MpaService {
    private final FilmStorage filmStorage;
    private static final Logger logger = LoggerFactory.getLogger(MpaService.class);

    @Autowired
    public MpaService(FilmDbStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public Collection<Mpa> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    @GetMapping
    public Mpa getMpaById(int id) {
        return filmStorage.getMpaById(id);
    }
}
