package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class FilmDaoImpl implements FilmDao {
    JdbcTemplate jdbcTemplate;
    MpaDao mpaDao;
    GenreDao genreDao;
    DirectorDao directorDao;

    @Autowired
    public FilmDaoImpl(JdbcTemplate jdbcTemplate, MpaDaoImpl mpaDaoImpl,
                       GenreDaoImpl genreDaoImpl, DirectorDao directorDao) {

        this.jdbcTemplate = jdbcTemplate;
        this.mpaDao = mpaDaoImpl;
        this.genreDao = genreDaoImpl;
        this.directorDao = directorDao;
    }

    @Override
    public Film createFilm(Film film) {
        long filmId = film.getId();
        String sqlFilmQuery = "insert into films (film_id, description, name, release_date, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlFilmQuery, filmId, film.getDescription(), film.getName(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());

        if (film.getGenres() != null) {
            Set<Genre> genresSet = new HashSet<>(film.getGenres());
            if (genresSet.size() != 0) {
                for (Genre genre : genresSet) {
                    int genreId = genre.getId();
                    String sqlGenreQuery = "insert into films_genres (film_id, genre_id) values (?, ?)";
                    jdbcTemplate.update(sqlGenreQuery, filmId, genreId);
                }
            }
        }

        directorDao.addFilmDirectors(film);
        return getFilmById(filmId);
    }

    @Override
    public Film getFilmById(long id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where film_id = ?", id);

        if (filmRows.next()) {
            Film film = new Film(
                    filmRows.getLong("film_id"),
                    filmRows.getString("description"),
                    filmRows.getString("name"),
                    Objects.requireNonNull(filmRows.getDate("release_date")).toLocalDate(),
                    filmRows.getInt("duration"),
                    createMpa(filmRows.getInt("mpa_id")),
                    new ArrayList<>());

            setFilmGenresAndDirectors(film);
            return film;
        } else {
            throw new DataNotFoundException(String.format("Фильм с id #%d  отсутствует в базе.", id));
        }
    }

    @Override
    public Film updateFilm(Film film) {
        long filmId = film.getId();
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select * from films where film_id = ?", film.getId());

        if (filmRows.next()) {
            String sqlQuery = "update films set name = ?, description = ?, release_date = ?, " +
                    "duration = ?, mpa_id = ? where film_id = ?";

            jdbcTemplate.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getMpa().getId(), film.getId());

            sqlQuery = "DELETE FROM FILMS_GENRES WHERE FILM_ID = ?";
            jdbcTemplate.update(sqlQuery, filmId);

            if (film.getGenres() != null) {
                List<Genre> genres = film.getGenres();
                Set<Genre> genresSet = new HashSet<>(genres);

                if (genresSet.size() != 0) {
                    for (Genre genre : genresSet) {
                        int genreId = genre.getId();
                        String sqlGenreQuery = "insert into films_genres (film_id, genre_id) values (?, ?)";
                        jdbcTemplate.update(sqlGenreQuery, filmId, genreId);
                    }
                }
            }

            directorDao.deleteFilmDirectors(filmId);
            directorDao.addFilmDirectors(film);
            return getFilmById(filmId);
        } else {
            throw new DataNotFoundException(String.format("Фильм с id #%d  отсутствует в базе.", film.getId()));
        }
    }

    @Override
    public Collection<Film> findAllFilms() {
        String sqlQuery = "select * from films";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm);

        for (Film film : films) {
            setFilmGenresAndDirectors(film);
        }
        return films;
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {

        return Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(createMpa(resultSet.getInt("mpa_id")))
                .genres(new ArrayList<>())
                .build();
    }

    private void setFilmGenresAndDirectors(Film film) {
        String sqlQuery = "SELECT G.GENRE_ID, G.NAME " +
                "FROM FILMS AS F " +
                "LEFT OUTER JOIN FILMS_GENRES AS FG ON F.FILM_ID = FG.FILM_ID " +
                "LEFT OUTER JOIN GENRES AS G ON FG.GENRE_ID = G.GENRE_ID " +
                "WHERE F.FILM_ID = ? AND G.GENRE_ID IS NOT NULL " +
                "ORDER BY G.GENRE_ID ASC";

        List<Genre> genres = jdbcTemplate.query(sqlQuery, genreDao::mapRowToGenres, film.getId());

        film.getGenres().clear();
        film.getGenres().addAll(genres);

        film.setDirectors(directorDao.getFilmDirectors(film.getId()));
    }

    private Mpa createMpa(int id) {
        return mpaDao.createMpa(id);
    }

    @Override
    public Map<Long, Film> getFilms() {
        Collection<Film> filmList = findAllFilms();
        Map<Long, Film> films = new HashMap<>();

        for (Film film : filmList) {
            films.put(film.getId(), film);
        }
        return films;
    }

    @Override
    public void addLike(long filmId, long userId) throws DataAlreadyExistException {
        String sqlQuery = "delete from likes where (film_id = ? and user_id = ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);

        sqlQuery = "insert into likes (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sqlQuery = "delete from likes where id = (select id from likes where (film_id = ? and user_id = ?))";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Film> getPopular(long count) {
        String sqlQuery = "SELECT F.* " +
                "FROM FILMS AS F " +
                "LEFT JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.USER_ID) DESC " +
                "LIMIT ?";

        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);

        for (Film film : films) {
            setFilmGenresAndDirectors(film);
        }
        return films;
    }

    @Override
    public Collection<Film> getAllDirectorsFilmsOrderByReleaseDate(long id) {
        String sql = "select f.* " +
                "from films as f " +
                "right join film_directors as fd on fd.film_id = f.film_id " +
                "where fd.director_id = ?" +
                "order by extract(year from f.release_date)";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);

        for (Film film : films) {
            setFilmGenresAndDirectors(film);
        }
        return films;
    }

    @Override
    public Collection<Film> getAllDirectorsFilmsOrderByLikes(long id) {
        String sql = "SELECT f.* " +
                "FROM FILMS AS f " +
                "right join film_directors as fd on fd.film_id = f.film_id " +
                "LEFT JOIN LIKES AS L ON Fd.FILM_ID = L.FILM_ID " +
                "WHERE fd.director_id = ?" +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.USER_ID)";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);

        for (Film film : films) {
            setFilmGenresAndDirectors(film);
        }
        return films;
    }

    @Override
    public void deleteFilm(long id) {
        String sqlQuery = "delete from films where film_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void deleteAll() {
        String sql = "delete from films CASCADE;";
        jdbcTemplate.update(sql);
    }

    @Override
    public List<Film> getRecommendations(long id) {
        String sqlQuery = "SELECT f.*\n" +
                "FROM likes AS l\n" +
                "INNER JOIN films AS f ON l.film_id = f.film_id\n" +
                "WHERE l.user_id = (SELECT ls.user_id\n" +
                "                   FROM likes AS ls\n" +
                "                   WHERE ls.film_id IN (SELECT l2.film_id\n" +
                "                                        FROM likes l2\n" +
                "                                        WHERE l2.user_id = ?)\n" +
                "                   AND ls.user_id != ?\n" +
                "                   GROUP BY ls.user_id\n" +
                "                   ORDER BY COUNT(ls.film_id) DESC \n" +
                "                   LIMIT 1)\n" +
                "AND l.film_id NOT IN (SELECT l3.film_id\n" +
                "                      FROM likes l3\n" +
                "                      WHERE l3.user_id = ?)";
        List<Film> films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, id, id, id);

        for (Film film : films) {
            setFilmGenresAndDirectors(film);
        }
        return films;
    }

    @Override
    public List<Film> searchFilm(String query, String by) {
        String lowerCaseQuery = "%" + query.toLowerCase() + "%";
        List<Film> films = new ArrayList<>();

        if (by.equals("title")) {
            String sqlQuery = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID " +
                    "FROM FILMS AS F " +
                    "LEFT OUTER JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                    "WHERE LOWER(F.NAME) LIKE ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(L.USER_ID) DESC";
            films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, lowerCaseQuery);

            for (Film film : films) {
                setFilmGenresAndDirectors(film);
            }
        }

        if (by.equals("director")) {
            String sqlQuery = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID " +
                    "FROM FILMS AS F " +
                    "LEFT OUTER JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                    "LEFT OUTER JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID " +
                    "LEFT OUTER JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                    "WHERE LOWER(D.NAME) LIKE ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(L.USER_ID) DESC";
            films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, lowerCaseQuery);

            for (Film film : films) {
                setFilmGenresAndDirectors(film);
            }
        }

        if (by.equals("title,director") || by.equals("director,title")) {
            String sqlQuery = "SELECT F.FILM_ID, F.NAME, F.DESCRIPTION, F.RELEASE_DATE, F.DURATION, F.MPA_ID " +
                    "FROM FILMS AS F " +
                    "LEFT OUTER JOIN LIKES AS L ON F.FILM_ID = L.FILM_ID " +
                    "LEFT OUTER JOIN FILM_DIRECTORS AS FD ON F.FILM_ID = FD.FILM_ID " +
                    "LEFT OUTER JOIN DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                    "WHERE LOWER(F.NAME) LIKE ? " +
                    "OR LOWER(D.NAME) LIKE ? " +
                    "GROUP BY F.FILM_ID " +
                    "ORDER BY COUNT(L.USER_ID) DESC";
            films = jdbcTemplate.query(sqlQuery, this::mapRowToFilm, lowerCaseQuery, lowerCaseQuery);

            for (Film film : films) {
                setFilmGenresAndDirectors(film);
            }
        }
        return films;
    }

    @Override
    public List<Film> getCommonFilms(long userId, long friendId) {
        String sql = "SELECT F.* " +
                "FROM FILMS AS F " +
                "RIGHT JOIN LIKES AS L ON L.FILM_ID = F.FILM_ID " +
                "WHERE L.FILM_ID IN (SELECT film_id from likes where user_id = ?) " +
                "   AND L.FILM_ID IN (SELECT film_id from likes where user_id = ?) " +
                "GROUP BY F.FILM_ID " +
                "ORDER BY COUNT(L.USER_ID )";

        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, userId, friendId);
        for (Film film : films) {
            setFilmGenresAndDirectors(film);
        }
        return films;
    }

    @Override
    public List<Film> getAllPopularFilmsOrderByLikes(long count, Integer genreId, Integer year) {
        List<Film> films = new ArrayList<>();

        if (genreId == null && year != null) {
            String sql = "SELECT f.* " +
                    "FROM films AS f " +
                    "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                    "LEFT JOIN films_genres AS g ON f.film_id = g.film_id " +
                    "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";

            films = jdbcTemplate.query(sql, this::mapRowToFilm, year, count);
        }

        if (year == null && genreId != null) {
            String sql = "SELECT f.* " +
                    "FROM films AS f " +
                    "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                    "LEFT JOIN films_genres AS g ON f.film_id = g.film_id " +
                    "WHERE g.genre_id = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";

            films = jdbcTemplate.query(sql, this::mapRowToFilm, genreId, count);
        }

        if (year != null && genreId != null) {
            String sql = "SELECT f.* " +
                    "FROM films AS f " +
                    "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                    "LEFT JOIN films_genres AS g ON f.film_id = g.film_id " +
                    "WHERE EXTRACT(YEAR FROM f.release_date) = ? " +
                    "AND g.genre_id = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";

            films = jdbcTemplate.query(sql, this::mapRowToFilm, year, genreId, count);
        }

        for (Film film : films) {
            setFilmGenresAndDirectors(film);
        }
        return films;
    }
}