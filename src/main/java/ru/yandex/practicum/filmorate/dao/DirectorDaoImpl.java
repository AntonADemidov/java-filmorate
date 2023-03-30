package ru.yandex.practicum.filmorate.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

@Component
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private RowMapper<Director> getDirectorRowMapper() {
        return (rs, rowNum) -> {
            Director director = new Director();
            director.setId(rs.getLong("director_id"));
            director.setName(rs.getString("name"));

            return director;
        };
    }

    @Override
    public void createDirector(Director director) throws ValidationException {
        validation(director);
        String sqlQuery = "insert into directors (name) values (?)";
        jdbcTemplate.update(sqlQuery,
                director.getName());
    }

    private void validation(Director director) throws ValidationException {
        if (director.getName().isBlank())
            throw new ValidationException("Имя режиссера не может быть пустым");
    }

    @Override
    public Director getLastAddedDirector() {
        return jdbcTemplate.query("select * " +
                        "from directors " +
                        "order by director_id desc " +
                        "limit 1",
                getDirectorRowMapper()).get(0);
    }

    @Override
    public void updateDirector(Director director) throws ValidationException {
        validation(director);
        String sql = "update directors set name = ? where director_id = ?";
        jdbcTemplate.update(sql,
                director.getName(),
                director.getId());
    }

    @Override
    public void deleteDirectorById(long id) {
        String sql = "delete from film_directors where director_id = ?";
        jdbcTemplate.update(sql, id);

        sql = "delete from directors where director_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Collection<Director> getAllDirectors() {
        String sql = "select * from directors";

        return jdbcTemplate.query(sql, getDirectorRowMapper());
    }

    @Override
    public Director getDirectorById(long id) {
        String sql = "select * from directors where director_id = ?";
        SqlRowSet directorRow = jdbcTemplate.queryForRowSet(sql, id);

        if (directorRow.next()) {

            Director director = new Director();
            director.setId(directorRow.getLong("director_id"));
            director.setName(directorRow.getString("name"));

            return director;
        } else {
            throw new DataNotFoundException("Режиссер с указанным id отсутствует в базе.");
        }
    }

    @Override
    public List<Director> getFilmDirectors(long filmId) {
        String sql = "select d.director_id, d.name " +
                "FROM film_directors as fd " +
                "left join directors AS d on fd.director_id = d.director_id " +
                "WHERE fd.FILM_ID = ?";

        return jdbcTemplate.query(sql, getDirectorRowMapper(), filmId);
    }

    @Override
    public void deleteFilmDirectors(long filmId) {
        String sql = "delete from film_directors where film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void addFilmDirectors(Film film) {
        String sql = "insert into film_directors (film_id, director_id) values (?, ?)";
        if (film.getDirectors() != null) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(sql, film.getId(), director.getId());
            }
        }
    }

    @Override
    public void deleteAll() {
        String sql = "delete from directors CASCADE;" +
                "ALTER TABLE directors ALTER COLUMN director_id RESTART WITH 1";
        jdbcTemplate.update(sql);
    }
}
