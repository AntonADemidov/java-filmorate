package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.DirectorDaoImpl;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DirectorTests {

    private final DirectorDaoImpl directorDao;
    private final FilmDbStorage filmDbStorage;

    @Order(1)
    @Test
    public void createDirectorTest() throws ValidationException {
        Director director = new Director();
        director.setName("Steven Spielberg");

        directorDao.createDirector(director);
        Director addedDirector = directorDao.getDirectorById(1);
        assertThat(addedDirector).hasFieldOrPropertyWithValue("name", "Steven Spielberg");
    }

    @Order(2)
    @Test
    public void updateDirectorTest() throws ValidationException {
        Director director = new Director();
        director.setId(1);
        director.setName("Сергей Бондарчук");

        directorDao.updateDirector(director);
        Director addedDirector = directorDao.getDirectorById(1);
        assertThat(addedDirector).hasFieldOrPropertyWithValue("name", "Сергей Бондарчук");
    }


    @Order(3)
    @Test
    public void addDirectorTest() throws Exception {
        Director director = directorDao.getDirectorById(1);
        Film newFilm = new Film(1, "New film about friends", "New film",
                LocalDate.of(1999, 4, 30), 120, new Mpa(3),
                List.of(new Genre(1), new Genre(2), new Genre(3)));

        newFilm.setDirectors(List.of(director));
        filmDbStorage.createFilm(newFilm);

        newFilm = new Film(2, "New film update decription", "Film Updated",
                LocalDate.of(1989, 4, 17), 190, new Mpa(2), List.of(new Genre(2)));
        newFilm.setDirectors(List.of(director));
        filmDbStorage.createFilm(newFilm);


        assertEquals(1, directorDao.getFilmDirectors(2L).size());
        assertEquals("Сергей Бондарчук", directorDao.getFilmDirectors(2L).get(0).getName());

        assertEquals(2, filmDbStorage.getDirectorFilmsOrderByLikes(1L).size());
        assertEquals(2, filmDbStorage.getDirectorFilmsOrderByYear(1L).size());
    }

    @Order(4)
    @Test
    public void deleteDirectorTest() {
        directorDao.deleteDirectorById(1);

        Collection<Director> directors = directorDao.getAllDirectors();
        assertEquals(0, directors.size());

        Film film = filmDbStorage.getFilmById(1L);
        assertEquals(0, film.getDirectors().size());
        film = filmDbStorage.getFilmById(2L);
        assertEquals(0, film.getDirectors().size());

        assertEquals(0, filmDbStorage.getDirectorFilmsOrderByLikes(1L).size());
        assertEquals(0, filmDbStorage.getDirectorFilmsOrderByYear(1L).size());

        directorDao.deleteAll();
        filmDbStorage.deleteAll();
    }
}
