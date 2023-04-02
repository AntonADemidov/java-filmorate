package ru.yandex.practicum.filmorate.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Feed;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Component
public class FeedDaoImpl implements FeedDao {
    private final Logger log = LoggerFactory.getLogger(FilmDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    private static final int ADD = 1;
    private static final int REMOVE = 2;
    private static final int UPDATE = 3;
    private static final int LIKE = 1;
    private static final int FRIEND = 2;
    private static final int REVIEW = 3;


    public FeedDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Feed> getFeeds(long userId) {
        String sqlQuery = "SELECT F.EVENT_ID, F.TIME_STAMP, F.USER_ID, F.ENTITY_ID, ET.EVENT_NAME, O.OPERATION_NAME\n" +
                "FROM FEEDS AS F\n" +
                "LEFT OUTER JOIN EVENTS_TYPES AS ET ON F.EVENT_TYPE = ET.TYPE_ID\n" +
                "LEFT OUTER JOIN OPERATIONS AS O ON F.OPERATION = O.OPERATION_ID\n" +
                "WHERE F.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFeed, userId);
    }

    private Feed mapRowToFeed(ResultSet resultSet, int rowNum) throws SQLException {
        return Feed.builder()
                .timestamp(resultSet.getLong("TIME_STAMP"))
                .userId(resultSet.getLong("USER_ID"))
                .eventType(resultSet.getString("EVENT_NAME"))
                .operation(resultSet.getString("OPERATION_NAME"))
                .eventId(resultSet.getLong("EVENT_ID"))
                .entityId(resultSet.getLong("ENTITY_ID"))
                .build();
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sqlQuery = "INSERT INTO FEEDS (TIME_STAMP, EVENT_TYPE, OPERATION, USER_ID, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Instant.now().toEpochMilli(), FRIEND, ADD, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sqlQuery = "INSERT INTO FEEDS (TIME_STAMP, EVENT_TYPE, OPERATION, USER_ID, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Instant.now().toEpochMilli(), FRIEND, REMOVE, userId, friendId);
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sqlQuery = "INSERT INTO FEEDS (TIME_STAMP, EVENT_TYPE, OPERATION, USER_ID, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Instant.now().toEpochMilli(), LIKE, ADD, userId, filmId);
    }

    @Override
    public void removeLike(long filmId, long userId) {
        String sqlQuery = "INSERT INTO FEEDS (TIME_STAMP, EVENT_TYPE, OPERATION, USER_ID, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Instant.now().toEpochMilli(), LIKE, REMOVE, userId, filmId);
    }

    @Override
    public void addReview(long userId, long reviewId) {
        String sqlQuery = "INSERT INTO FEEDS (TIME_STAMP, EVENT_TYPE, OPERATION, USER_ID, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Instant.now().toEpochMilli(), REVIEW, ADD, userId, reviewId);
    }

    @Override
    public void removeReview(long userId, long reviewId) {
        String sqlQuery = "INSERT INTO FEEDS (TIME_STAMP, EVENT_TYPE, OPERATION, USER_ID, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Instant.now().toEpochMilli(), REVIEW, REMOVE, userId, reviewId);
    }

    @Override
    public void updateReview(long userId, long reviewId) {
        String sqlQuery = "INSERT INTO FEEDS (TIME_STAMP, EVENT_TYPE, OPERATION, USER_ID, ENTITY_ID) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, Instant.now().toEpochMilli(), REVIEW, UPDATE, userId, reviewId);
    }
}