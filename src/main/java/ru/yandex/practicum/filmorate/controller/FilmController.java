package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FilmController {
    FilmService filmService;
    UserService userService;
    private static final String actionWithLikes = "/{id}/like/{userId}";
    private static final String actionWithId = "/{id}";

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) throws Throwable {
        return filmService.createFilm(film);
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws Throwable {
        return filmService.updateFilm(film);
    }

    @GetMapping(actionWithId)
    public Film getFilmById(@PathVariable long id) throws DataNotFoundException {
        return filmService.getFilmById(id);
    }

    @PutMapping(actionWithLikes)
    public void addLike(@PathVariable long id, @PathVariable long userId) throws DataAlreadyExistException {
        filmService.addLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) long count,
                                      @RequestParam(value = "genreId", required = false) Integer genreId,
                                      @RequestParam(value = "year", required = false) Integer year) {

        if (year == null && genreId == null) {
            return filmService.getPopular(count);
        }
        return filmService.getAllPopularFilmsOrderByLikes(count, genreId, year);
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

    @DeleteMapping(actionWithId)
    public void deleteFilm(@PathVariable long id) {
        filmService.deleteFilm(id);
    }

    @GetMapping("/search")
    public List<Film> searchFilm(@RequestParam(value = "query", required = true) String query,
                                 @RequestParam(value = "by", required = true) String by) throws ValidationException {
        return filmService.searchFilm(query, by);
    }

    @GetMapping("common")
    public List<Film> getSharedFilms(@RequestParam(value = "userId", required = true) long userId,
                                     @RequestParam(value = "friendId", required = true) long friendId) {
        return userService.getCommonFilms(userId, friendId);
    }
}