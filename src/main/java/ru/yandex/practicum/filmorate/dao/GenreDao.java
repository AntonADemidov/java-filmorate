package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public interface GenreDao {

    void validateGenre(Genre genre);

    String validateGenre(int id);

    Genre createGenre(int id);

    Collection<Genre> getAllGenres();

    Genre mapRowToGenres (ResultSet resultSet, int rowNum) throws SQLException;

    Genre getGenreById(int id);
}
