package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {
    /**
     * Добавление нового отзыва с сохранением в БД
     *
     * @param review добавляемый отзыв
     * @return id добавленного отзыва
     */
    int createReview(Review review);

    /**
     * Обновление отзыва в БД
     *
     * @param review - обновленный отзыв
     * @return id обновленного отзыва
     */
    int updateReview(Review review);

    /**
     * Удаление отзыва по id
     *
     * @param id - id удаляемого отзыва
     */
    void deleteReviewById(int id);

    /**
     * Получение отзыва из БД по id
     *
     * @param id - id запрашиваемого отзыва
     * @return - отзыв
     */
    Review getReviewById(int id);

    /**
     * Получение отзывов по id фильма с указанием количества.
     *
     * @param filmId - id фильма, отзывы которого надо получить
     * @param amount - количество отзывов. Если 0, то возвращаются все отзывы.
     * @return - Список отзывов фильма
     */
    List<Review> getReviewsByFilm(int filmId, int amount);

    /**
     * Создание ОЦЕНКИ (лайка или дизлайка) для отзыва от конкретного пользователя
     *
     * @param reviewId - id Отзыва
     * @param userId   - id пользователя
     * @param value    - +1 лайк, -1 дизлайк
     */
    void createLike(int reviewId, int userId, int value);

    /**
     * Удаление ОЦЕНКИ (лайка или дизлайка) у отзыва от конкретного пользователя
     *
     * @param reviewId - id Отзыва
     * @param userId   - id пользователя
     */
    void deleteLike(int reviewId, int userId);

}
