package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Service
@Slf4j
public class ReviewService {
    private final ReviewDao reviewDao;
    private final UserService userService;
    private final FilmService filmService;

    @Autowired
    public ReviewService(ReviewDao reviewDao, UserService userService, FilmService filmService) {
        this.reviewDao = reviewDao;
        this.userService = userService;
        this.filmService = filmService;
    }

    /**
     * создание нового отзыва
     *
     * @param review - новый отзыв
     * @return - отзыв
     */
    public Review createReview(Review review) {

        User user = userService.getUserById(review.getUserId());
        Film film = filmService.getFilmById(review.getFilmId());
        if (user == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        if (film == null) {
            throw new DataNotFoundException("Пользователь не найден");
        }
        int newId = reviewDao.createReview(review); //сохранили в БД
        return reviewDao.getReviewById(newId); //вернули объект отзыв из БД
    }

    /**
     * обновление отзыва в БД
     *
     * @param updatedReview - обновленный отзыв
     */
    public Review update(Review updatedReview) {
        Integer updatedReviewId = updatedReview.getReviewId(); //из переданного элемента взали Id

        if (reviewDao.getReviewById(updatedReviewId) == null) { //если не существует - исключение
            log.warn("Ошибка обновления: не найден элемент");
            throw new DataNotFoundException("Ошибка обновления данных: отзыв не найден");
        }
        int id = reviewDao.updateReview(updatedReview); //обновили данные в хранилище

        return reviewDao.getReviewById(id); //вернули объект отзыв из БД
    }

    /**
     * Получение одного отзыва из БД по Id
     *
     * @param id - Id требуемого отзыва
     * @return отзыв
     */
    public Review getById(int id) {
        Review review = reviewDao.getReviewById(id);
        if (review == null) {
            throw new DataNotFoundException("Отзыв не найден");
        }
        return review;
    }

    /**
     * Удаление отзыва по id
     *
     * @param id - id отзыва
     */
    public void deleteReviewById(int id) {
        reviewDao.deleteReviewById(id);
    }

    /**
     * Метод добавления лайка от конкретного пользователя на отзыв
     *
     * @param reviewId - Id отзыва
     * @param userId   - Id пользователя
     */
    public void addReviewLike(int reviewId, int userId) {
        reviewDao.createLike(reviewId, userId, 1);
    }

    /**
     * Метод добавления дизлайка от конкретного пользователя на отзыв
     *
     * @param reviewId - Id отзыва
     * @param userId   - Id пользователя
     */
    public void addReviewDislike(int reviewId, int userId) {
        reviewDao.createLike(reviewId, userId, -1);
    }


    /**
     * Метод удаления лайка от конкретного пользователя на отзыв
     *
     * @param reviewId - Id отзыва
     * @param userId   - Id пользователя
     */
    public void removeReviewLike(int reviewId, int userId) {
        reviewDao.deleteLike(reviewId, userId);
    }

    /**
     * Получение отзывов на фильм.
     *
     * @param filmId - id фильма. Если 0, то проходим по всем фильмам
     * @param count  - Количество выводимых отзывов
     * @return - список отзывов
     */
    public List<Review> getAllReviews(int filmId, int count) {
        return reviewDao.getReviewsByFilm(filmId, count);
    }
}
