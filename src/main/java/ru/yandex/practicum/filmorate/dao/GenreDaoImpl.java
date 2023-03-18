package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
public class GenreDaoImpl implements GenreDao {
    private final Logger log = LoggerFactory.getLogger(GenreDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void validateGenre(Genre genre) {

    }

    @Override
    public String validateGenre(int id) {
        return null;
    }

    @Override
    public Genre createGenre(int id) {
        String sqlQuery = "select * from genres where genre_id = ?";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToGenres, id);
    }

    @Override
    public Collection<Genre> getAllGenres() {
        String sqlQuery = "select * from genres";
        return jdbcTemplate.query(sqlQuery, this::mapRowToGenres);
    }

    @Override
    public Genre mapRowToGenres(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("genre_id"))
                .name(resultSet.getString("name"))
                .build();
    }

    @Override
    public Genre getGenreById(int id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genres where genre_id = ?", id);

        if (genreRows.next()) {
            Genre genre = createGenre(id);
            log.info("Найден GENRE: {} {}", genre.getId(), genre.getName());
            return genre;
        } else {
            throw new DataNotFoundException("GENRE с указанным id отсутствует в базе.");
        }
    }
}