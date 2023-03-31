package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.DirectorDaoImpl;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
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

    @Order(1)
    @Test
    public void createUserTest() throws Exception {
        User user = new User(1, "Nick Name", "mail@mail.ru", "dolore", LocalDate.of(1946, 8, 20));
        User newUser = userDbStorage.createUser(user);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "Nick Name");
        assertThat(newUser).hasFieldOrPropertyWithValue("login", "dolore");
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "mail@mail.ru");
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1946, 8, 20));
    }

    @Order(2)
    @Test
    public void updateUserTest() throws Exception {
        User user = new User(1, "est adipisicing", "mail@yandex.ru", "doloreUpdate", LocalDate.of(1976, 9, 20));
        User newUser = userDbStorage.updateUser(user);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "est adipisicing");
        assertThat(newUser).hasFieldOrPropertyWithValue("login", "doloreUpdate");
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "mail@yandex.ru");
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1976, 9, 20));
    }

    @Order(3)
    @Test
    public void findAllUsersTest() {
        Collection<User> users = userDbStorage.findAllUsers();
        assertEquals(1, users.size());
        for (User newUser : users) {
            assertThat(newUser).hasFieldOrPropertyWithValue("id", 1L);
            assertThat(newUser).hasFieldOrPropertyWithValue("name", "est adipisicing");
            assertThat(newUser).hasFieldOrPropertyWithValue("login", "doloreUpdate");
            assertThat(newUser).hasFieldOrPropertyWithValue("email", "mail@yandex.ru");
            assertThat(newUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1976, 9, 20));
        }
    }

    @Order(4)
    @Test
    public void createFriendTest() throws Exception {
        User user = new User(2, "friend adipisicing", "friend@mail.ru", "friend", LocalDate.of(1976, 8, 20));
        User newUser = userDbStorage.createUser(user);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", 2L);
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "friend adipisicing");
        assertThat(newUser).hasFieldOrPropertyWithValue("login", "friend");
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "friend@mail.ru");
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1976, 8, 20));
    }

    @Order(5)
    @Test
    public void createCommonFriendTest() throws Exception {
        User user = new User(3, "", "friend@common.ru", "common", LocalDate.of(2000, 8, 20));
        User newUser = userDbStorage.createUser(user);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", 3L);
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "common");
        assertThat(newUser).hasFieldOrPropertyWithValue("login", "common");
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "friend@common.ru");
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 8, 20));
    }

    @Order(6)
    @Test
    public void getUserByIdTest1() {
        User newUser = userDbStorage.getUserById(1);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "est adipisicing");
        assertThat(newUser).hasFieldOrPropertyWithValue("login", "doloreUpdate");
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "mail@yandex.ru");
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1976, 9, 20));
    }

    @Order(7)
    @Test
    public void getUserByIdTest2() {
        User newUser = userDbStorage.getUserById(2);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", 2L);
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "friend adipisicing");
        assertThat(newUser).hasFieldOrPropertyWithValue("login", "friend");
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "friend@mail.ru");
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1976, 8, 20));
    }

    @Order(8)
    @Test
    public void getCommonFriendsTest() {
        List<User> users = userDbStorage.getCommonFriends(1, 2);
        assertEquals(0, users.size());
    }

    @Order(9)
    @Test
    public void getFriendsTest() {
        List<User> users = userDbStorage.getFriends(1);
        assertEquals(0, users.size());
    }

    @Order(10)
    @Test
    public void addFriendTest() throws DataAlreadyExistException {
        userDbStorage.addFriend(1, 2);

        List<User> users = userDbStorage.getFriends(1);
        assertEquals(1, users.size());
        assertEquals(2L, users.get(0).getId());
        assertEquals("friend adipisicing", users.get(0).getName());
        assertEquals("friend@mail.ru", users.get(0).getEmail());
        assertEquals("friend", users.get(0).getLogin());
        assertEquals(LocalDate.of(1976, 8, 20), users.get(0).getBirthday());
    }

    @Order(11)
    @Test
    public void getCommonFriendsTest2() {
        List<User> users = userDbStorage.getCommonFriends(1, 2);
        assertEquals(0, users.size());
    }

    @Order(12)
    @Test
    public void addFriendTest2() throws DataAlreadyExistException {
        userDbStorage.addFriend(1, 3);

        List<User> users = userDbStorage.getFriends(1);
        assertEquals(2, users.size());

        assertEquals(2L, users.get(0).getId());
        assertEquals("friend adipisicing", users.get(0).getName());
        assertEquals("friend@mail.ru", users.get(0).getEmail());
        assertEquals("friend", users.get(0).getLogin());
        assertEquals(LocalDate.of(1976, 8, 20), users.get(0).getBirthday());

        assertEquals(3L, users.get(1).getId());
        assertEquals("common", users.get(1).getName());
        assertEquals("friend@common.ru", users.get(1).getEmail());
        assertEquals("common", users.get(1).getLogin());
        assertEquals(LocalDate.of(2000, 8, 20), users.get(1).getBirthday());
    }

    @Order(13)
    @Test
    public void addFriendTest3() throws DataAlreadyExistException {
        userDbStorage.addFriend(2, 3);

        List<User> users = userDbStorage.getFriends(2);
        assertEquals(1, users.size());

        assertEquals(3L, users.get(0).getId());
        assertEquals("common", users.get(0).getName());
        assertEquals("friend@common.ru", users.get(0).getEmail());
        assertEquals("common", users.get(0).getLogin());
        assertEquals(LocalDate.of(2000, 8, 20), users.get(0).getBirthday());
    }


    @Order(14)
    @Test
    public void getCommonFriendsTest3() throws DataAlreadyExistException {
        userDbStorage.addFriend(2, 1);

        List<User> users = userDbStorage.getCommonFriends(2, 1);
        assertEquals(1, users.size());

        assertEquals(3L, users.get(0).getId());
        assertEquals("common", users.get(0).getName());
        assertEquals("friend@common.ru", users.get(0).getEmail());
        assertEquals("common", users.get(0).getLogin());
        assertEquals(LocalDate.of(2000, 8, 20), users.get(0).getBirthday());
    }

    @Order(15)
    @Test
    public void removeFriendTest() throws DataAlreadyExistException {
        userDbStorage.removeFriend(1, 2);

        List<User> users = userDbStorage.getFriends(1);
        assertEquals(1, users.size());

        assertEquals(3L, users.get(0).getId());
        assertEquals("common", users.get(0).getName());
        assertEquals("friend@common.ru", users.get(0).getEmail());
        assertEquals("common", users.get(0).getLogin());
        assertEquals(LocalDate.of(2000, 8, 20), users.get(0).getBirthday());
    }

    @Order(16)
    @Test
    public void findAllFilmsTest() {
        Collection<Film> films = filmDbStorage.findAllFilms();
        assertEquals(0, films.size());
    }

    @Order(17)
    @Test
    public void createFilmTest() throws Exception {
        Film film = new Film(1, "adipisicing", "nisi eiusmod", LocalDate.of(1967, 03, 25), 100, new Mpa(1), new ArrayList<>());
        Film newFilm = filmDbStorage.createFilm(film);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "adipisicing");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "nisi eiusmod");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1967, 03, 25));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 100);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(1, "G"));
        assertEquals(0, newFilm.getGenres().size());
    }

    @Order(18)
    @Test
    public void updateFilmTest() throws Exception {
        Film film = new Film(1, "New film update decription", "Film Updated", LocalDate.of(1989, 04, 17), 190, new Mpa(2), new ArrayList<>());
        Film newFilm = filmDbStorage.updateFilm(film);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "New film update decription");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "Film Updated");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1989, 04, 17));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 190);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
        assertEquals(0, newFilm.getGenres().size());
    }

    @Order(19)
    @Test
    public void findAllFilmsTest2() {
        Collection<Film> films = filmDbStorage.findAllFilms();
        assertEquals(1, films.size());

        for (Film film : films) {
            assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
            assertThat(film).hasFieldOrPropertyWithValue("description", "New film update decription");
            assertThat(film).hasFieldOrPropertyWithValue("name", "Film Updated");
            assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1989, 04, 17));
            assertThat(film).hasFieldOrPropertyWithValue("duration", 190);
            assertThat(film).hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
            assertEquals(0, film.getGenres().size());
        }
    }

    @Order(20)
    @Test
    public void getPopularTest() {
        List<Film> films = filmDbStorage.getPopular(10);
        assertEquals(1, films.size());

        for (Film film : films) {
            assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
            assertThat(film).hasFieldOrPropertyWithValue("description", "New film update decription");
            assertThat(film).hasFieldOrPropertyWithValue("name", "Film Updated");
            assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1989, 4, 17));
            assertThat(film).hasFieldOrPropertyWithValue("duration", 190);
            assertThat(film).hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
            assertEquals(0, film.getGenres().size());
        }
    }

    @Order(21)
    @Test
    public void createFilmTest2() throws Exception {
        Film film = new Film(2, "New film about friends", "New film",
                LocalDate.of(1999, 4, 30), 120, new Mpa(3), List.of(new Genre(1)));
        Film newFilm = filmDbStorage.createFilm(film);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 2L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "New film about friends");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "New film");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1999, 4, 30));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 120);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(3, "PG-13"));
        assertThat(newFilm).hasFieldOrPropertyWithValue("genres", List.of(new Genre(1, "Комедия")));
        assertEquals(1, newFilm.getGenres().size());
    }

    @Order(22)
    @Test
    public void getFilmByIdTest() {
        Film newFilm = filmDbStorage.getFilmById(1);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "New film update decription");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "Film Updated");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1989, 04, 17));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 190);
        assertEquals(0, newFilm.getGenres().size());
    }

    @Order(23)
    @Test
    public void addLikeTest() throws DataAlreadyExistException {
        filmDbStorage.addLike(2, 1);

        List<Film> films = filmDbStorage.getPopular(10);
        assertEquals(2, films.size());
        assertEquals(2L, films.get(0).getId());
        assertEquals(1L, films.get(1).getId());
    }

	@Order(24)
	@Test
	public void addLikeTest2() throws DataAlreadyExistException {
		filmDbStorage.addLike(1, 1);
		filmDbStorage.addLike(1, 2);

		List<Film> films = filmDbStorage.getPopular(10);
		assertEquals(2, films.size());
		assertEquals(1L, films.get(0).getId());
		assertEquals(2L, films.get(1).getId());
	}

	@Order(25)
	@Test
	public void getListRecommendationsFilms() {
		List<Film> films = filmDbStorage.getRecommendationsFilms(2);

		assertEquals(1, films.size());
		assertEquals(2, films.get(0).getId());
	}

    @Order(26)
    @Test
    public void getMostPopularMoviesByYearAndGenreParameter() {
        List<Film> films = filmDbStorage.getAllPopularFilmsOrderByLikes(2, 1, 1999);

        assertEquals(1, films.size());

        films = filmDbStorage.getAllPopularFilmsOrderByLikes(2, null, 1999);

        assertEquals(1, films.size());

        films = filmDbStorage.getAllPopularFilmsOrderByLikes(2, 1, null);

        assertEquals(1, films.size());
    }

	@Order(27)
	@Test
	public void removeLikeTest() throws DataAlreadyExistException {
		filmDbStorage.removeLike(1, 1);
		filmDbStorage.removeLike(1, 2);

        List<Film> films = filmDbStorage.getPopular(10);
        assertEquals(2, films.size());
        assertEquals(2L, films.get(0).getId());
        assertEquals(1L, films.get(1).getId());
    }

	@Order(28)
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

	@Order(29)
	@Test
	public void getAllMpaTest() throws DataAlreadyExistException {
		Collection<Mpa> mpaList = filmDbStorage.getAllMpa();
		assertEquals(5, mpaList.size());
	}

	@Order(30)
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

	@Order(31)
	@Test
	public void getAllGenreTest() {
		Collection<Genre> genreList = filmDbStorage.getAllGenres();
		assertEquals(6, genreList.size());
	}

	@Order(32)
	@Test
	public void updateFilmTest2() throws Exception {
		Film film = new Film(1,"New film update decription", "Film Updated",
				LocalDate.of(1989,04,17), 190, new Mpa(2), List.of(new Genre(2)));
		Film newFilm = filmDbStorage.updateFilm(film);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "New film update decription");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "Film Updated");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1989, 04, 17));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 190);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
        assertThat(newFilm).hasFieldOrPropertyWithValue("genres", List.of(new Genre(2, "Драма")));
        assertEquals(1, newFilm.getGenres().size());
    }

	@Order(33)
	@Test
	public void getFilmByIdTest2() {
		Film newFilm = filmDbStorage.getFilmById(1);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "New film update decription");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "Film Updated");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1989, 04, 17));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 190);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
        assertThat(newFilm).hasFieldOrPropertyWithValue("genres", List.of(new Genre(2, "Драма")));
        assertEquals(1, newFilm.getGenres().size());
    }

	@Order(34)
	@Test
	public void updateFilmTest3() throws Exception {
		Film film = new Film(1,"New film update decription", "Film Updated",
				LocalDate.of(1989,04,17), 190, new Mpa(5), new ArrayList<>());
		Film newFilm = filmDbStorage.updateFilm(film);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "New film update decription");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "Film Updated");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1989, 04, 17));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 190);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(5, "NC-17"));
        assertThat(newFilm).hasFieldOrPropertyWithValue("genres", new ArrayList<>());
        assertEquals(0, newFilm.getGenres().size());
    }

	@Order(35)
	@Test
	public void updateFilmTest4() throws Exception {
		Film film = new Film(2,"New film about friends", "New film",
				LocalDate.of(1999,4,30), 120, new Mpa(3),
				List.of(new Genre(1), new Genre(2), new Genre(3)));
		Film newFilm = filmDbStorage.updateFilm(film);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 2L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "New film about friends");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "New film");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1999, 4, 30));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 120);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(3, "PG-13"));
        assertThat(newFilm).hasFieldOrPropertyWithValue("genres",
                List.of(new Genre(1, "Комедия"), new Genre(2, "Драма"), new Genre(3, "Мультфильм")));
        assertEquals(3, newFilm.getGenres().size());
    }

	@Order(36)
	@Test
	public void updateFilmTest5() throws Exception {
		Film film = new Film(2,"New film about friends", "New film",
				LocalDate.of(1999,4,30), 120, new Mpa(3),
				List.of(new Genre(1), new Genre(2), new Genre(1)));
		Film newFilm = filmDbStorage.updateFilm(film);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 2L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "New film about friends");
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "New film");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1999, 4, 30));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 120);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(3, "PG-13"));
        assertThat(newFilm).hasFieldOrPropertyWithValue("genres",
                List.of(new Genre(1, "Комедия"), new Genre(2, "Драма")));
        assertEquals(2, newFilm.getGenres().size());
    }

    @Order(37)
    @Test
    public void searchFilmTest1() throws ValidationException {
        List<Film> films = filmDbStorage.searchFilm("DAT", "title");
        assertEquals(1L, films.get(0).getId());
    }

    @Order(38)
    @Test
    public void searchFilmTest2() throws ValidationException {
        List<Film> films = filmDbStorage.searchFilm("NO FILM", "title");
        assertEquals(0, films.size());
    }

    @Order(39)
    @Test
    public void searchFilmTest3() throws Exception {
        Director director = new Director();
        director.setName("Steven Spielberg");

        directorDao.createDirector(director);
        Director addedDirector = directorDao.getDirectorById(1);

        Film film = new Film(3, "New film with director", "New film with director",
                LocalDate.of(1999, 4, 30), 120, new Mpa(3),
                List.of(new Genre(1), new Genre(2)));

        film.setDirectors(List.of(addedDirector));

        filmDbStorage.createFilm(film);

        List<Film> films = filmDbStorage.searchFilm("ber", "director");
        assertEquals(3L, films.get(0).getId());
    }

    @Order(40)
    @Test
    public void searchFilmTest4() throws Exception {
        Director director = new Director();
        director.setName("NONAME");

        directorDao.createDirector(director);
        Director addedDirector = directorDao.getDirectorById(2);

        Film film = new Film(4, "New film with NONAME director", "SEVEN",
                LocalDate.of(1999, 4, 30), 120, new Mpa(3),
                List.of(new Genre(1), new Genre(2)));

        film.setDirectors(List.of(addedDirector));

        filmDbStorage.createFilm(film);

        List<Film> films = filmDbStorage.searchFilm("eVeN", "director,title");
        assertEquals(2, films.size());
    }

    @Order(100)
    @Test
    public void deleteAll() {
        userDbStorage.deleteAll();
        filmDbStorage.deleteAll();
    }
}