package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ReviewDaoImpl implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int createReview(Review review) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate).withTableName("reviews")
                .usingGeneratedKeyColumns("review_id");
        int createdId = simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue();
        review.setReviewId(createdId);
        return createdId;
    }

    @Override
    public int updateReview(Review review) {
        /*вычисление полезности отзыва по лайкам*/
        int useful = 0;
        for (int like : review.getLikedUsers().values()) {
            useful += like; //суммируем оценки
        }

        String sqlQuery = "update reviews set content = ?, useful = ?,  isPositive = ? where review_id = ?";
        /*заполняем параметры для запроса в БД. Складываем в него обновляемые данные*/
        int reviewId = review.getReviewId();
        jdbcTemplate.update(sqlQuery, review.getContent(), useful, review.getIsPositive(), reviewId);

        return review.getReviewId();
    }

    @Override
    public void deleteReviewById(int id) {
        String sqlQuery = "delete from reviews where review_id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Review getReviewById(int id) {
        /*получаем отзыв из БД*/
        String sqlQuery = "select * " +
                "FROM reviews AS r " +
                "WHERE review_id = ?"; //запрос для получения отзыва

        Review review = new Review();

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, id);
        if (userRows.next()) {
            review.setReviewId(userRows.getInt("review_id"));
            review.setFilmId(userRows.getInt("film_id"));
            review.setUserId(userRows.getInt("user_id"));
            review.setContent(userRows.getString("content"));
            review.setUseful(userRows.getInt("useful"));
            review.setIsPositive(userRows.getBoolean("isPositive"));
        } else {
            throw new DataNotFoundException("Ошибка получения данных: отзыв не найден");
        }
        /*Складываем в Отзыв мапу из id пользователей и их оценок, полученных по id отзыва*/
        review.setLikedUsers(getLikesByReview(id));
        return review;
    }

    @Override
    public List<Review> getReviewsByFilm(int filmId, int amount) {
        List<Review> reviewList = new ArrayList<>();
        String sqlQuery = "";

        if (filmId == 0) {
            //запрос без ограничения по id
            sqlQuery = "select * from reviews ORDER BY useful DESC LIMIT ? ;";
            reviewList = jdbcTemplate.query(sqlQuery, this::mapRowToReview, amount);
        } else {
            //запрос по конкретному фильму
            sqlQuery = "select * from reviews where film_id = ? ORDER BY useful DESC LIMIT ?;";
            reviewList = jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, amount);
        }
        setLikesForReviewList(reviewList); //устанавливаем для всех отзывов их оценки
        return reviewList;
    }

    @Override
    public void createLike(int reviewId, int userId, int value) {
        Map<Integer, Integer> likedUsers = this.getLikedUsersByReview(reviewId);//взяли из БД все лайки и дизлайки для Отзыва
        likedUsers.put(userId, value); //Добавили новый лайк/дизлайк (или заменили лайк/дизлайк, если оценка уже стоит

        /*добавляем новые данные в таблицу review_likes*/
        final String sqlInsertQuery = "merge into review_likes(review_id,user_id,isUseful) KEY (review_id,user_id) values(?,?,?)";
        likedUsers.forEach((id, like) -> jdbcTemplate.update(sqlInsertQuery, reviewId, id, like));

        /*после добавления лайка пересчитываем популярность отзыва*/
        updateReview(getReviewById(reviewId)); //при обновлении пересчитывается популярность
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        Map<Integer, Integer> likedUsers = this.getLikedUsersByReview(reviewId);//взяли из БД все лайки и дизлайки для Отзыва
        likedUsers.remove(userId); //Удалили оценку отзыва

        /*удалили данные из таблицы review_likes*/
        final String sqlInsertQuery = "delete from review_likes where review_id = ? AND user_id = ?";
        jdbcTemplate.update(sqlInsertQuery, reviewId, userId);

        /*после удаления лайка пересчитываем популярность отзыва*/
        updateReview(getReviewById(reviewId)); //при обновлении пересчитывается популярность
    }


    /**
     * Получение из БД всех пользователей, лайкнувших отзыв.
     *
     * @param reviewId - id отзыва
     * @return Set из id пользователей
     */
    private Map<Integer, Integer> getLikedUsersByReview(int reviewId) {
        Map<Integer, Integer> users = new HashMap<>();
        /*получаем пользователей, лайкнувших отзыв из БД*/
        String sqlQuery = "select * from review_likes where review_id = ? ORDER BY user_id"; //запрос для получения пользователей по отзыву
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, reviewId); //отправка запроса и сохранение результатов в SqlRowSet
        /*превращаем полученные записи в сет объектов типа Integer*/
        while (userRows.next()) { //проходим по всем строкам, извлекаем id и складываем в сет
            users.put(userRows.getInt("user_id"), userRows.getInt("isUseful"));
        }
        return users;
    }

    /**
     * Получение из БД всех оценок, соответствующих отзыву.
     *
     * @param reviewId - id Отзыва
     * @return Map<id пользователя, его оценка ( лайк-дизлайк )>
     */
    private Map<Integer, Integer> getLikesByReview(int reviewId) {
        Map<Integer, Integer> likes = new HashMap<>();
        /*получаем оценки отзыва из БД*/
        String sqlQuery = "select * " +
                "from review_likes AS rl " +
                "where review_id = ? " +
                "ORDER BY user_id"; //запрос для получения жанров фильма

        SqlRowSet userRows = jdbcTemplate.queryForRowSet(sqlQuery, reviewId); //отправка запроса и сохранение результатов в SqlRowSet
        /*превращаем полученные данные в Map<id пользователя, его оценка (лайк-дизлайк)>*/
        while (userRows.next()) { //проходим по всем строкам, извлекаем id пользователя и его оценку и складываем в мапу
            int user_id = userRows.getInt("user_id"); //извлекаем значение
            int isUseful = userRows.getInt("isUseful"); //извлекаем значение
            likes.put(user_id, isUseful);
        }
        return likes;
    }

    /**
     * Сохранение в БД в таблицу review_likes оценок переданного отзыва.
     *
     * @param review - отзыв, оценки которого надо сохранить в БД
     */
    private void saveReviewLikes(Review review) {
        if (review == null || review.getLikedUsers().isEmpty()) {
            return;
        }
        Map<Integer, Integer> likes = review.getLikedUsers();
        ArrayList<Integer> users = new ArrayList<>(likes.keySet());
        ArrayList<Integer> isUseful = new ArrayList<>(likes.values());
        int reviewId = review.getReviewId();

        jdbcTemplate.batchUpdate("insert into review_likes(review_id,user_id,isUseful) values(?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setInt(1, reviewId);
                        preparedStatement.setInt(2, users.get(i));
                        preparedStatement.setInt(3, isUseful.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return users.size();
                    }
                });
    }

    /**
     * Добавление лайков к каждому отзыву из списка отзывов (через таблицу review_likes).
     *
     * @param reviewList - список отзывов
     */
    private void setLikesForReviewList(List<Review> reviewList) {
        /*из списка отзывов получаем список их id. И создаем строку для условия запроса*/
        List<String> reviewIdlist = reviewList.stream()
                .map(f -> String.valueOf(f.getReviewId()))
                .collect(Collectors.toList());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        if (reviewIdlist.size() != 0) {
            reviewIdlist.forEach(f -> stringBuilder.append(f + ","));
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(")");

        String sql = "SELECT review_id, user_id, isUseful " +
                "FROM review_likes " +
                "WHERE user_id IN " + stringBuilder.toString() + ";";

        /*преобразуем ArrayList в мапу <id, review>*/
        Map<Integer, Review> reviewMap = reviewList.stream().collect(Collectors.toMap(Review::getReviewId, review -> review));
        RowCallbackHandler rowCallbackHandler = new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                int userId = rs.getInt("user_id");
                int reviewId = rs.getInt("review_id");
                int like = rs.getInt("isUseful");
                reviewMap.get(reviewId).addUserLike(userId, like); //добавление лайка к отзыву
            }
        };
        jdbcTemplate.query(sql, rowCallbackHandler); //выполняем запрос и обработку результатов
    }

    /**
     * Метод для имплементации функционального интерфейса RowMapper, описывающий
     * превращение данных из ResultSet в объект типа Review
     *
     * @param resultSet считанный из БД набор данных
     * @param rowNum    номер строки
     * @return объект Review
     * @throws SQLException
     */
    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        Review review = new Review();

        review.setReviewId(resultSet.getInt("review_id"));
        review.setFilmId(resultSet.getInt("film_id"));
        review.setUserId(resultSet.getInt("user_id"));
        review.setContent(resultSet.getString("content"));
        review.setUseful(resultSet.getInt("useful"));
        review.setIsPositive(resultSet.getBoolean("isPositive"));
        return review;
    }
}
