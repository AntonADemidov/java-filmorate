package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FilmDbStorage implements FilmStorage {
    FilmDao filmDao;
    MpaDao mpaDao;
    GenreDao genreDao;
    FeedDao feedDao;
    UserStorage userStorage;
    @NonFinal
    int idCounter = 0;

    @Autowired
    public FilmDbStorage(FilmDaoImpl filmDaoImpl, MpaDaoImpl mpaDaoImpl, GenreDaoImpl genreDaoImpl,
                         FeedDaoImpl feedDaoImpl, UserDbStorage userDbStorage) {
        this.filmDao = filmDaoImpl;
        this.mpaDao = mpaDaoImpl;
        this.genreDao = genreDaoImpl;
        this.feedDao = feedDaoImpl;
        this.userStorage = userDbStorage;
    }

    @Override
    public Film createFilm(Film film) throws Exception {
        validateFilm(film);
        film.setId(++idCounter);
        return filmDao.createFilm(film);
    }

    @Override
    public Collection<Film> findAllFilms() {
        return filmDao.findAllFilms();
    }

    @Override
    public Film updateFilm(Film film) throws Exception {
        validateFilm(film);
        return filmDao.updateFilm(film);
    }

    @Override
    public Map<Long, Film> getFilms() {
        return filmDao.getFilms();
    }

    @Override
    public Film getFilmById(long filmId) {
        return filmDao.getFilmById(filmId);
    }

    @Override
    public Collection<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }

    @Override
    public Collection<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }

    @Override
    public Mpa getMpaById(int id) {
        return mpaDao.getMpaById(id);
    }

    @Override
    public Genre getGenreById(int id) {
        return genreDao.getGenreById(id);
    }

    @Override
    public void addLike(long filmId, long userId) throws DataAlreadyExistException {
        validateFilmAndUser(filmId, userId);
        filmDao.addLike(filmId, userId);
        feedDao.addLike(filmId, userId);
    }

    @Override
    public List<Film> getPopular(long count) {
        return filmDao.getPopular(count);
    }

    @Override
    public void removeLike(long filmId, long userId) throws DataAlreadyExistException {
        validateFilmAndUser(filmId, userId);
        filmDao.removeLike(filmId, userId);
        feedDao.removeLike(filmId, userId);
    }

    @Override
    public List<Film> getRecommendationsFilms(long userId) {
        return filmDao.getRecommendations(userId);
    }

    private void validateFilm(Film film) throws Exception {
        if (!(film.getDescription().length() <= 200)) {
            throw new ValidationException("Необходимо добавить описание фильма (параметр description: до 200 символов.");
        }

        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Необходимо добавить дату релиза (параметр releaseDate: не ранее 28 декабря 1895 года.");
        }

        if (film.getDuration() == null || film.getDuration() < 0) {
            throw new ValidationException("Необходимо добавить продолжительность фильма (параметр duration: положительный).");
        }
    }

    private void validateFilmAndUser(long filmId, long userId) throws DataNotFoundException {
        if (!getFilms().containsKey(filmId)) {
            throw new DataNotFoundException(String.format("Фильм с id # %d отсутствует в базе.", filmId));
        }

        if (!userStorage.getUsers().containsKey(userId)) {
            throw new DataNotFoundException(String.format("Пользователь с id # %d отсутствует в базе.", userId));
        }
    }

    @Override
    public Collection<Film> getDirectorFilmsOrderByLikes(long directorId) {
        return filmDao.getAllDirectorsFilmsOrderByLikes(directorId);
    }

    @Override
    public Collection<Film> getDirectorFilmsOrderByYear(long directorId) {
        return filmDao.getAllDirectorsFilmsOrderByReleaseDate(directorId);
    }

    @Override
    public void deleteFilm(long id) {
        filmDao.deleteFilm(id);
    }

    @Override
    public void deleteAll() {
        idCounter = 0;
        filmDao.deleteAll();
    }

    @Override
    public List<Film> searchFilm(String query, String by) throws ValidationException {
        validateSearch(query, by);
        return filmDao.searchFilm(query, by);
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        return filmDao.getCommonFilms(userId, friendId);
    }

    private void validateSearch(String query, String by) throws ValidationException {
        if (query.isBlank() || by.isBlank()) {
            throw new ValidationException("Поиск по пробелам не осуществляется.");
        }

        if (!by.equals("title")
                && !by.equals("director")
                && !by.equals("title,director")
                && !by.equals("director,title")
        ) {
            throw new ValidationException("Параметры поиска заданы не верно");
        }
    }

    @Override
    public List<Film> getAllPopularFilmsOrderByLikes(long count, Integer genreId, Integer year) {
        return filmDao.getAllPopularFilmsOrderByLikes(count, genreId, year);
    }
}