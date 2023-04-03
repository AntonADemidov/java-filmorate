package ru.yandex.practicum.filmorate.dao;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UserDaoImpl implements UserDao {
    JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "insert into users (user_id, name, email, login, birthday) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlQuery, user.getId(), user.getName(), user.getEmail(), user.getLogin(), user.getBirthday());
        return getUserById(user.getId());
    }

    @Override
    public User updateUser(User user) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", user.getId());

        if (userRows.next()) {
            String sqlQuery = "update users set name = ?, email = ?, login = ?, birthday = ? where user_id = ?";
            jdbcTemplate.update(sqlQuery, user.getName(), user.getEmail(), user.getLogin(), user.getBirthday(), user.getId());
            return getUserById(user.getId());
        } else {
            throw new DataNotFoundException(String.format("Пользователь с id #%d отсутствует в базе.", user.getId()));
        }
    }

    @Override
    public void addFriend(long userId, long friendId) throws DataAlreadyExistException {
        String sqlQuery = "delete from friends where (user_id = ? and friend_id = ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);

        sqlQuery = "insert into friends (user_id, friend_id) values (?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }


    @Override
    public User getUserById(long id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from users where user_id = ?", id);

        if (userRows.next()) {
            return new User(
                    userRows.getLong("user_id"),
                    userRows.getString("name"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate());
        } else {
            throw new DataNotFoundException(String.format("Пользователь с id #%d отсутствует в базе.", id));
        }
    }

    @Override
    public Collection<User> findAllUsers() {
        String sqlQuery = "select * from users";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("user_id"))
                .name(resultSet.getString("name"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public Map<Long, User> getUsers() {
        Collection<User> userList = findAllUsers();
        Map<Long, User> users = new HashMap<>();

        for (User user : userList) {
            users.put(user.getId(), user);
        }
        return users;
    }

    @Override
    public List<User> getFriends(long id) {
        String sqlQuery = "SELECT U.USER_ID, U.NAME, U.EMAIL, U.LOGIN, U.BIRTHDAY FROM FRIENDS AS F " +
                "LEFT OUTER JOIN USERS AS U ON F.FRIEND_ID = U.USER_ID WHERE F.USER_ID = ?";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id);
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) throws DataNotFoundException {

        final List<User> userFriends = getFriends(userId);
        final List<User> otherUserFriends = getFriends(otherUserId);
        final List<User> commonFriends = new ArrayList<>();

        for (User data : userFriends) {
            if (otherUserFriends.contains(data)) {
                commonFriends.add(data);
            }
        }
        return commonFriends;
    }

    @Override
    public void deleteUser(long id) {
        String sqlQuery = "delete from users where user_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void deleteAll() {
        String sql = "delete from users CASCADE;";
        jdbcTemplate.update(sql);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sqlQuery = "delete from friends where id = (select id from friends where (user_id = ? and friend_id = ?))";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }
}