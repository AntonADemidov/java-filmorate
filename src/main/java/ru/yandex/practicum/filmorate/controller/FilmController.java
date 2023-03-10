package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable long id) throws DataNotFoundException {
        return filmService.getFilmById(id);
    }

    /*Валидация данных из тела запроса происходит в классе FilmService c помощью метода validateFilm.
    Никита, добрый день,

    В качестве результата работы метода либо пробрасывается ValidationException и создание объекта прекращается,
    либо, если все данные соответствуют условиям, создается и сохраняется в хранилище соответствующий объект.

    После вашего вопроса в метод валидации validateFilm была добавлена проверка полей на null c пробросом общего
    исключения Exception и выводом сообщения для пользователя о необходимости указания всех обязательных параметров
    в теле запроса, необходимых для корректного создания объекта.

    Такой подход, на мой взгляд, вполне соответствует пункту ТЗ про HTTP-коды ("500 — если возникло исключение."),
    так как проверка тела запроса на наличие необходимой информации (есть или null) логически является
    предварительной ступенью валидации перед последующей валидацией самой информации в полях объекта по заданным условиям.

    Аналогичные изменения применил и к связке классов UserController - UserService (метод validateUser).

    Буду благодарен за обратную связь, и да пребудет с вами сила!*/
    @PostMapping
    public Film createFilm(@RequestBody Film film) throws Throwable {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws Throwable {
        return filmService.updateFilm(film);
    }

    @PutMapping(actionWithLikes)
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(actionWithLikes)
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) long count) {
        return filmService.getPopular(count);
    }
}