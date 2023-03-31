package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final String actionWithLikes = "/{id}/like/{userId}";

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) throws Throwable {
        return filmService.createFilm(film);
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws Throwable {
        return filmService.updateFilm(film);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) throws DataNotFoundException {
        return filmService.getFilmById(id);
    }

    @PutMapping(actionWithLikes)
    public void addLike(@PathVariable long id, @PathVariable long userId) throws DataAlreadyExistException {
        filmService.addLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) long count) {
        return filmService.getPopular(count);
    }

    @DeleteMapping(actionWithLikes)
    public void removeLike(@PathVariable long id, @PathVariable long userId) throws DataAlreadyExistException {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getDirectorFilms(@PathVariable long directorId, @RequestParam String sortBy) {
        if (sortBy.equals("likes")) {
            return filmService.getDirectorFilmsOrderByLikes(directorId);
        }

        if (sortBy.equals("year")) {
            return filmService.getDirectorFilmsOrderByYear(directorId);
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) {
        filmService.deleteFilm(id);
    }

    @GetMapping("/search")
    public List<Film> searchFilm(@RequestParam(value = "query", required = true) String query,
                                 @RequestParam(value = "by", required = true) String by) throws ValidationException {
        return filmService.searchFilm(query, by);
    }
}