package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class FilmDaoImpl implements FilmDao {
    private final Logger log = LoggerFactory.getLogger(FilmDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final DirectorDao directorDao;

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
                    filmRows.getDate("release_date").toLocalDate(),
                    filmRows.getInt("duration"),
                    createMpa(filmRows.getInt("mpa_id")),
                    new ArrayList<>());

            setFilmGenresAndDirectors(film);
            return film;
        } else {
            throw new DataNotFoundException("Фильм с указанным id отсутствует в базе.");
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
            throw new DataNotFoundException("Фильм с указанным id отсутствует в базе.");
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
        Film film = Film.builder()
                .id(resultSet.getLong("film_id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getInt("duration"))
                .mpa(createMpa(resultSet.getInt("mpa_id")))
                .genres(new ArrayList<>())
                .build();

        return film;
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
    public void removeLike(long filmId, long userId) throws DataAlreadyExistException {
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
}