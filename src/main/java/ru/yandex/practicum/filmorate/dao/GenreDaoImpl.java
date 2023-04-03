package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GenreDaoImpl implements GenreDao {
    JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
            throw new DataNotFoundException(String.format("Жанр с id #%d отсутствует в базе", id));
        }
    }
}