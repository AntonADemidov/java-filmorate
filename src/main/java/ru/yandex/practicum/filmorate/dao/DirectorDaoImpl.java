package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DirectorDaoImpl implements DirectorDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
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
    public void createDirector(Director director) {
        String sqlQuery = "insert into directors (name) values (?)";
        jdbcTemplate.update(sqlQuery,
                director.getName());
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
    public void updateDirector(Director director) {
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
            throw new DataNotFoundException(String.format("Режиссер с id #%d отсутствует в базе.", id));
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