package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.DirectorDaoImpl;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmorateApplicationTests {
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;
    private final DirectorDaoImpl directorDao;
    private final ReviewService reviewService;

    @Order(1)
    @Test
    public void userTests() throws Exception {
        User user = new User(1, "Nick Name", "mail@mail.ru", "dolore",
                LocalDate.of(1946, 8, 20));
        User newUser = userDbStorage.createUser(user);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "Nick Name");
        assertThat(newUser).hasFieldOrPropertyWithValue("login", "dolore");
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "mail@mail.ru");
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday"
                , LocalDate.of(1946, 8, 20));

        user = new User(1, "est adipisicing", "mail@yandex.ru", "doloreUpdate",
                LocalDate.of(1976, 9, 20));
        newUser = userDbStorage.updateUser(user);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "est adipisicing");
        assertThat(newUser).hasFieldOrPropertyWithValue("login", "doloreUpdate");
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "mail@yandex.ru");
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday"
                , LocalDate.of(1976, 9, 20));

        Collection<User> users = userDbStorage.findAllUsers();
        assertEquals(1, users.size());
        for (User UserFromList : users) {
            assertThat(UserFromList).hasFieldOrPropertyWithValue("id", 1L);
            assertThat(UserFromList).hasFieldOrPropertyWithValue("name", "est adipisicing");
            assertThat(UserFromList).hasFieldOrPropertyWithValue("login", "doloreUpdate");
            assertThat(UserFromList).hasFieldOrPropertyWithValue("email", "mail@yandex.ru");
            assertThat(UserFromList).hasFieldOrPropertyWithValue("birthday",
                    LocalDate.of(1976, 9, 20));
        }

        User user2 = new User(2, "friend adipisicing", "friend@mail.ru", "friend",
                LocalDate.of(1976, 8, 20));
        userDbStorage.createUser(user2);

        User userById2 = userDbStorage.getUserById(2);

        assertThat(userById2).hasFieldOrPropertyWithValue("id", 2L);
        assertThat(userById2).hasFieldOrPropertyWithValue("name", "friend adipisicing");
        assertThat(userById2).hasFieldOrPropertyWithValue("login", "friend");
        assertThat(userById2).hasFieldOrPropertyWithValue("email", "friend@mail.ru");
        assertThat(userById2).hasFieldOrPropertyWithValue("birthday",
                LocalDate.of(1976, 8, 20));

        User friend = new User(3, "", "friend@common.ru", "common",
                LocalDate.of(2000, 8, 20));
        userDbStorage.createUser(friend);

        User userById3 = userDbStorage.getUserById(3);

        assertThat(userById3).hasFieldOrPropertyWithValue("id", 3L);
        assertThat(userById3).hasFieldOrPropertyWithValue("name", "common");
        assertThat(userById3).hasFieldOrPropertyWithValue("login", "common");
        assertThat(userById3).hasFieldOrPropertyWithValue("email", "friend@common.ru");
        assertThat(userById3).hasFieldOrPropertyWithValue("birthday",
                LocalDate.of(2000, 8, 20));

        List<User> commonFriends = userDbStorage.getCommonFriends(1, 2);
        assertEquals(0, commonFriends.size());

        List<User> friends = userDbStorage.getFriends(1);
        assertEquals(0, friends.size());

        userDbStorage.addFriend(1, 2);

        friends = userDbStorage.getFriends(1);
        assertEquals(1, friends.size());
        assertEquals(2L, friends.get(0).getId());
        assertEquals("friend adipisicing", friends.get(0).getName());
        assertEquals("friend@mail.ru", friends.get(0).getEmail());
        assertEquals("friend", friends.get(0).getLogin());
        assertEquals(LocalDate.of(1976, 8, 20), friends.get(0).getBirthday());

        commonFriends = userDbStorage.getCommonFriends(1, 2);
        assertEquals(0, commonFriends.size());

        userDbStorage.addFriend(1, 3);

        friends = userDbStorage.getFriends(1);
        assertEquals(2, friends.size());
        assertEquals(3L, friends.get(1).getId());
        assertEquals("common", friends.get(1).getName());
        assertEquals("friend@common.ru", friends.get(1).getEmail());
        assertEquals("common", friends.get(1).getLogin());
        assertEquals(LocalDate.of(2000, 8, 20), friends.get(1).getBirthday());

        userDbStorage.addFriend(2, 3);

        List<User> friendsUsers2 = userDbStorage.getFriends(2);
        assertEquals(1, friendsUsers2.size());

        assertEquals(3L, friendsUsers2.get(0).getId());
        assertEquals("common", friendsUsers2.get(0).getName());
        assertEquals("friend@common.ru", friendsUsers2.get(0).getEmail());
        assertEquals("common", friendsUsers2.get(0).getLogin());
        assertEquals(LocalDate.of(2000, 8, 20), friendsUsers2.get(0).getBirthday());

        userDbStorage.addFriend(2, 1);

        commonFriends = userDbStorage.getCommonFriends(2, 1);
        assertEquals(1, commonFriends.size());

        assertEquals(3L, commonFriends.get(0).getId());
        assertEquals("common", commonFriends.get(0).getName());
        assertEquals("friend@common.ru", commonFriends.get(0).getEmail());
        assertEquals("common", commonFriends.get(0).getLogin());
        assertEquals(LocalDate.of(2000, 8, 20), commonFriends.get(0).getBirthday());

        userDbStorage.removeFriend(1, 2);

        friends = userDbStorage.getFriends(1);
        assertEquals(1, friends.size());

        assertEquals(3L, friends.get(0).getId());
        assertEquals("common", friends.get(0).getName());
        assertEquals("friend@common.ru", friends.get(0).getEmail());
        assertEquals("common", friends.get(0).getLogin());
        assertEquals(LocalDate.of(2000, 8, 20), friends.get(0).getBirthday());
    }

    @Test
    public void filmTests() throws Exception {
        Collection<Film> filmList = filmDbStorage.findAllFilms();
        assertEquals(0, filmList.size());

        Film film = new Film(1, "adipisicing", "nisi eiusmod",
                LocalDate.of(1967, 03, 25), 100, new Mpa(1), new ArrayList<>());
        Film newFilm = filmDbStorage.createFilm(film);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "adipisicing");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "nisi eiusmod");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate",
                LocalDate.of(1967, 03, 25));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 100);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(1, "G"));
        assertEquals(0, newFilm.getGenres().size());

        Film filmUpdate = new Film(1, "New film update decription", "Film Updated",
                LocalDate.of(1989, 04, 17), 190, new Mpa(2), new ArrayList<>());
        filmDbStorage.updateFilm(filmUpdate);

        newFilm = filmDbStorage.getFilmById(1);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "New film update decription");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "Film Updated");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate",
                LocalDate.of(1989, 04, 17));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 190);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
        assertEquals(0, newFilm.getGenres().size());

        filmList = filmDbStorage.findAllFilms();
        assertEquals(1, filmList.size());

        for (Film filmFromList : filmList) {
            assertThat(filmFromList).hasFieldOrPropertyWithValue("id", 1L);
            assertThat(filmFromList).hasFieldOrPropertyWithValue("description", "New film update decription");
            assertThat(filmFromList).hasFieldOrPropertyWithValue("name", "Film Updated");
            assertThat(filmFromList).hasFieldOrPropertyWithValue("releaseDate",
                    LocalDate.of(1989, 04, 17));
            assertThat(filmFromList).hasFieldOrPropertyWithValue("duration", 190);
            assertThat(filmFromList).hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
            assertEquals(0, filmFromList.getGenres().size());
        }

        List<Film> popular = filmDbStorage.getPopular(10);
        assertEquals(1, popular.size());

        Film film2 = new Film(2, "New film about friends", "New film",
                LocalDate.of(1999, 4, 30), 120, new Mpa(3), List.of(new Genre(1)));
        Film newFilm2 = filmDbStorage.createFilm(film2);

        assertThat(newFilm2).hasFieldOrPropertyWithValue("id", 2L);
        assertThat(newFilm2).hasFieldOrPropertyWithValue("description", "New film about friends");
        assertThat(newFilm2).hasFieldOrPropertyWithValue("name", "New film");
        assertThat(newFilm2).hasFieldOrPropertyWithValue("releaseDate",
                LocalDate.of(1999, 4, 30));
        assertThat(newFilm2).hasFieldOrPropertyWithValue("duration", 120);
        assertThat(newFilm2).hasFieldOrPropertyWithValue("mpa", new Mpa(3, "PG-13"));
        assertThat(newFilm2).hasFieldOrPropertyWithValue("genres", List.of(new Genre(1, "Комедия")));
        assertEquals(1, newFilm2.getGenres().size());

        filmDbStorage.addLike(2, 1);

        popular = filmDbStorage.getPopular(10);
        assertEquals(2, popular.size());
        assertEquals(2L, popular.get(0).getId());
        assertEquals(1L, popular.get(1).getId());

        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);

        popular = filmDbStorage.getPopular(10);
        assertEquals(2, popular.size());
        assertEquals(1L, popular.get(0).getId());
        assertEquals(2L, popular.get(1).getId());

        List<Film> recommendationsFilms = filmDbStorage.getRecommendationsFilms(2);

        assertEquals(1, recommendationsFilms.size());
        assertEquals(2, recommendationsFilms.get(0).getId());

        List<Film> allPopularFilmsOrderByLikes = filmDbStorage.getAllPopularFilmsOrderByLikes(2, 1, 1999);

        assertEquals(1, allPopularFilmsOrderByLikes.size());

        allPopularFilmsOrderByLikes = filmDbStorage.getAllPopularFilmsOrderByLikes(2, null, 1999);

        assertEquals(1, allPopularFilmsOrderByLikes.size());

        allPopularFilmsOrderByLikes = filmDbStorage.getAllPopularFilmsOrderByLikes(2, 1, null);

        assertEquals(1, allPopularFilmsOrderByLikes.size());

        filmDbStorage.removeLike(1, 1);
        filmDbStorage.removeLike(1, 2);

        popular = filmDbStorage.getPopular(10);
        assertEquals(2, popular.size());
        assertEquals(2L, popular.get(0).getId());
        assertEquals(1L, popular.get(1).getId());

        Film filmUpdate2 = new Film(1,"New film update decription", "Film Updated",
                LocalDate.of(1989,04,17), 190, new Mpa(2), List.of(new Genre(2)));
        filmDbStorage.updateFilm(filmUpdate2);
        Film newFilmUpdate = filmDbStorage.getFilmById(1);

        assertThat(newFilmUpdate).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newFilmUpdate).hasFieldOrPropertyWithValue("description", "New film update decription");
        assertThat(newFilmUpdate).hasFieldOrPropertyWithValue("name", "Film Updated");
        assertThat(newFilmUpdate).hasFieldOrPropertyWithValue("releaseDate",
                LocalDate.of(1989, 04, 17));
        assertThat(newFilmUpdate).hasFieldOrPropertyWithValue("duration", 190);
        assertThat(newFilmUpdate).hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
        assertThat(newFilmUpdate).hasFieldOrPropertyWithValue("genres", List.of(new Genre(2, "Драма")));
        assertEquals(1, newFilmUpdate.getGenres().size());

        Film update = new Film(1,"New film update decription", "Film Updated",
                LocalDate.of(1989,04,17), 190, new Mpa(5), new ArrayList<>());
        Film filmAfterUpdate = filmDbStorage.updateFilm(update);

        assertThat(filmAfterUpdate).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(filmAfterUpdate).hasFieldOrPropertyWithValue("mpa", new Mpa(5, "NC-17"));
        assertThat(filmAfterUpdate).hasFieldOrPropertyWithValue("genres", new ArrayList<>());
        assertEquals(0, filmAfterUpdate.getGenres().size());

        Director director = new Director();
        director.setName("Steven Spielberg");

        directorDao.createDirector(director);
        Director addedDirector = directorDao.getDirectorById(1);

        Film update2 = new Film(2,"New film with director", "New film with director",
                LocalDate.of(1999,4,30), 120, new Mpa(3),
                List.of(new Genre(1), new Genre(2), new Genre(3)), List.of(addedDirector));

        filmAfterUpdate = filmDbStorage.updateFilm(update2);

        assertThat(filmAfterUpdate).hasFieldOrPropertyWithValue("id", 2L);
        assertThat(filmAfterUpdate).hasFieldOrPropertyWithValue("description", "New film with director");
        assertThat(filmAfterUpdate).hasFieldOrPropertyWithValue("name", "New film with director");
        assertThat(filmAfterUpdate).hasFieldOrPropertyWithValue("releaseDate",
                LocalDate.of(1999, 4, 30));
        assertThat(filmAfterUpdate).hasFieldOrPropertyWithValue("duration", 120);
        assertThat(filmAfterUpdate).hasFieldOrPropertyWithValue("mpa", new Mpa(3, "PG-13"));
        assertThat(filmAfterUpdate).hasFieldOrPropertyWithValue("genres",
                List.of(new Genre(1, "Комедия"), new Genre(2, "Драма"),
                        new Genre(3, "Мультфильм")));
        assertEquals(3, filmAfterUpdate.getGenres().size());

        List<Film> searchFilm = filmDbStorage.searchFilm("DAT", "title");
        assertEquals(1L, searchFilm.get(0).getId());

        searchFilm = filmDbStorage.searchFilm("NO FILM", "title");
        assertEquals(0, searchFilm.size());

        searchFilm = filmDbStorage.searchFilm("ber", "director");
        assertEquals(2L, searchFilm.get(0).getId());

        Director director2 = new Director();
        director2.setName("NONAME");

        directorDao.createDirector(director2);
        Director addedDirector2 = directorDao.getDirectorById(2);

        Film filmWithDirector = new Film(4, "New film with NONAME director", "SEVEN",
                LocalDate.of(1999, 4, 30), 120, new Mpa(3),
                List.of(new Genre(1), new Genre(2)));

        filmWithDirector.setDirectors(List.of(addedDirector2));

        filmDbStorage.createFilm(filmWithDirector);

        searchFilm = filmDbStorage.searchFilm("eVeN", "director,title");
        assertEquals(2, searchFilm.size());

        filmDbStorage.addLike(1, 1);
        filmDbStorage.addLike(1, 2);

        List<Film> films = filmDbStorage.getCommonFilms(1, 2);
        assertEquals(1, films.size());

        List<Feed> feeds = userDbStorage.getFeeds(1);
        assertEquals(7, feeds.size());

        assertEquals(1, feeds.get(0).getEventId());
        assertEquals(1, feeds.get(0).getUserId());
        assertEquals(2, feeds.get(0).getEntityId());
        assertEquals("ADD", feeds.get(0).getOperation());
        assertEquals("FRIEND", feeds.get(0).getEventType());

        assertEquals(2, feeds.get(1).getEventId());
        assertEquals(1, feeds.get(1).getUserId());
        assertEquals(3, feeds.get(1).getEntityId());
        assertEquals("ADD", feeds.get(1).getOperation());
        assertEquals("FRIEND", feeds.get(1).getEventType());

        assertEquals(5, feeds.get(2).getEventId());
        assertEquals(1, feeds.get(2).getUserId());
        assertEquals(2, feeds.get(2).getEntityId());
        assertEquals("REMOVE", feeds.get(2).getOperation());
        assertEquals("FRIEND", feeds.get(2).getEventType());

        assertEquals(6, feeds.get(3).getEventId());
        assertEquals(1, feeds.get(3).getUserId());
        assertEquals(2, feeds.get(3).getEntityId());
        assertEquals("ADD", feeds.get(3).getOperation());
        assertEquals("LIKE", feeds.get(3).getEventType());

        assertEquals(7, feeds.get(4).getEventId());
        assertEquals(1, feeds.get(4).getUserId());
        assertEquals(1, feeds.get(4).getEntityId());
        assertEquals("ADD", feeds.get(4).getOperation());
        assertEquals("LIKE", feeds.get(4).getEventType());

        assertEquals(9, feeds.get(5).getEventId());
        assertEquals(1, feeds.get(5).getUserId());
        assertEquals(1, feeds.get(5).getEntityId());
        assertEquals("REMOVE", feeds.get(5).getOperation());
        assertEquals("LIKE", feeds.get(5).getEventType());

        assertEquals(11, feeds.get(6).getEventId());
        assertEquals(1, feeds.get(6).getUserId());
        assertEquals(1, feeds.get(6).getEntityId());
        assertEquals("ADD", feeds.get(6).getOperation());
        assertEquals("LIKE", feeds.get(6).getEventType());

        List<Feed> feedsUser2 = userDbStorage.getFeeds(2);
        assertEquals(5, feedsUser2.size());

        assertEquals(3, feedsUser2.get(0).getEventId());
        assertEquals(2, feedsUser2.get(0).getUserId());
        assertEquals(3, feedsUser2.get(0).getEntityId());
        assertEquals("ADD", feedsUser2.get(0).getOperation());
        assertEquals("FRIEND", feedsUser2.get(0).getEventType());

        assertEquals(4, feedsUser2.get(1).getEventId());
        assertEquals(2, feedsUser2.get(1).getUserId());
        assertEquals(1, feedsUser2.get(1).getEntityId());
        assertEquals("ADD", feedsUser2.get(1).getOperation());
        assertEquals("FRIEND", feedsUser2.get(1).getEventType());

        assertEquals(8, feedsUser2.get(2).getEventId());
        assertEquals(2, feedsUser2.get(2).getUserId());
        assertEquals(1, feedsUser2.get(2).getEntityId());
        assertEquals("ADD", feedsUser2.get(2).getOperation());
        assertEquals("LIKE", feedsUser2.get(2).getEventType());

        assertEquals(10, feedsUser2.get(3).getEventId());
        assertEquals(2, feedsUser2.get(3).getUserId());
        assertEquals(1, feedsUser2.get(3).getEntityId());
        assertEquals("REMOVE", feedsUser2.get(3).getOperation());
        assertEquals("LIKE", feedsUser2.get(3).getEventType());

        assertEquals(12, feedsUser2.get(4).getEventId());
        assertEquals(2, feedsUser2.get(4).getUserId());
        assertEquals(1, feedsUser2.get(4).getEntityId());
        assertEquals("ADD", feedsUser2.get(4).getOperation());
        assertEquals("LIKE", feedsUser2.get(4).getEventType());

        Review review = new Review();
        review.setFilmId(1L);
        review.setUserId(1L);
        review.setIsPositive(true);
        review.setContent("Отзыв от user6 на film5");
        Review newReview = reviewService.createReview(review);

        feeds = userDbStorage.getFeeds(1);
        assertEquals(8, feeds.size());

        assertEquals(13, feeds.get(7).getEventId());
        assertEquals(1, feeds.get(7).getUserId());
        assertEquals(newReview.getReviewId(), feeds.get(7).getEntityId());
        assertEquals("ADD", feeds.get(7).getOperation());
        assertEquals("REVIEW", feeds.get(7).getEventType());

        Review review2 = new Review();
        review2.setReviewId(1);
        review2.setFilmId(1L);
        review2.setUserId(1L);
        review2.setIsPositive(false);
        review2.setContent("ОБНОВЛЕННЫЙ Отзыв 1");
        Review newReview2 = reviewService.update(review2);

        feeds = userDbStorage.getFeeds(1);
        assertEquals(9, feeds.size());

        assertEquals(14, feeds.get(8).getEventId());
        assertEquals(1, feeds.get(8).getUserId());
        assertEquals(newReview2.getReviewId(), feeds.get(8).getEntityId());
        assertEquals("UPDATE", feeds.get(8).getOperation());
        assertEquals("REVIEW", feeds.get(8).getEventType());

        reviewService.deleteReviewById(1);

        feeds = userDbStorage.getFeeds(1);
        assertEquals(10, feeds.size());

        assertEquals(15, feeds.get(9).getEventId());
        assertEquals(1, feeds.get(9).getUserId());
        assertEquals(1, feeds.get(9).getEntityId());
        assertEquals("REMOVE", feeds.get(9).getOperation());
        assertEquals("REVIEW", feeds.get(9).getEventType());

        userDbStorage.deleteAll();
        filmDbStorage.deleteAll();
    }

    @Test
    public void getMpaByIdTest() {
        Mpa mpa = filmDbStorage.getMpaById(1);
        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");

        Mpa mpa2 = filmDbStorage.getMpaById(3);
        assertThat(mpa2).hasFieldOrPropertyWithValue("id", 3);
        assertThat(mpa2).hasFieldOrPropertyWithValue("name", "PG-13");

        Mpa mpa3 = filmDbStorage.getMpaById(5);
        assertThat(mpa3).hasFieldOrPropertyWithValue("id", 5);
        assertThat(mpa3).hasFieldOrPropertyWithValue("name", "NC-17");

    }

    @Test
    public void getAllMpaTest() {
        Collection<Mpa> mpaList = filmDbStorage.getAllMpa();
        assertEquals(5, mpaList.size());
    }

    @Test
    public void getGenreByIdTest() {
        Genre genre = filmDbStorage.getGenreById(1);
        assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
        assertThat(genre).hasFieldOrPropertyWithValue("name", "Комедия");

        Genre genre2 = filmDbStorage.getGenreById(4);
        assertThat(genre2).hasFieldOrPropertyWithValue("id", 4);
        assertThat(genre2).hasFieldOrPropertyWithValue("name", "Триллер");

        Genre genre3 = filmDbStorage.getGenreById(6);
        assertThat(genre3).hasFieldOrPropertyWithValue("id", 6);
        assertThat(genre3).hasFieldOrPropertyWithValue("name", "Боевик");
    }

    @Test
    public void getAllGenreTest() {
        Collection<Genre> genreList = filmDbStorage.getAllGenres();
        assertEquals(6, genreList.size());
    }
}