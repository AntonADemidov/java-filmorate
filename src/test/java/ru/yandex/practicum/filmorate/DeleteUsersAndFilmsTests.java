package ru.yandex.practicum.filmorate;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteUsersAndFilmsTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Order(1)
    @Test
    public void deleteUsersAndFilmsTest() throws Exception {
        User user = new User(1, "Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        user = userDbStorage.createUser(user);

        User friend = new User(2, "friend adipisicing", "friend@mail.ru", "friend", LocalDate.of(1976, 8, 20));
        friend = userDbStorage.createUser(friend);

        userDbStorage.addFriend(1, 2);

        Film film = new Film(1, "adipisicing", "nisi eiusmod", LocalDate.of(1967, 03, 25), 100, new Mpa(1), new ArrayList<>());
        film = filmDbStorage.createFilm(film);

        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);

        userDbStorage.deleteUser(1);
        assertEquals(1, userDbStorage.getUsers().size());

        assertEquals(0, userDbStorage.getFriends(2).size());

        filmDbStorage.deleteFilm(1);
        assertEquals(0, filmDbStorage.getFilms().size());
    }
}
