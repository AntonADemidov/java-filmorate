package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dao.ReviewDaoImpl;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/schema.sql", "/data_reviews.sql"}) //перед каждым тестом запускается создание исходной базы по заданным скриптам
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ReviewTests {

    private final ReviewDaoImpl reviewDao;

    /**
     * Проверка метода получения отзыва по id (getReviewById).
     * Запрашиваем существующий отзыв по его id и проверяем,
     * что приняты нужные данные
     */
    @Test
    public void testGetReviewById() {
        /*В optional помещаю отзыв, полученный из БД с помощью тестируемого метода*/
        Optional<Review> reviewOptional = Optional.of(reviewDao.getReviewById(1));

        /*проверяем, что получили нужные данные*/
        assertThat(reviewOptional)
                .isPresent() //проверка, что optional не пустой
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("reviewId", 1))
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("filmId", 1))
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("userId", 1)
                ); //проверка, что полученные данные соответствуют ожидаемым
    }

    /**
     * Проверка метода создания нового отзыва (createReview)
     * Создаем отзыв с конкретными параметрами, после чего запрашиваем этот отзыв
     * из БД и проверяем, что получен именно от. Т.е. что он был сохранен в БД.
     */
    @Test
    public void testCreateReview() {
        /*создаем и конфигурируем отзыв*/
        Review newReview = new Review();
        newReview.setFilmId(5L);
        newReview.setUserId(6L);
        newReview.setIsPositive(true);
        newReview.setContent("Отзыв от user6 на film5");

        int id = reviewDao.createReview(newReview); //сохранили отзыв в БД с помощью тестируемого метода
        Optional<Review> userOptional = Optional.of(reviewDao.getReviewById(id));//В optional помещаем отзыв, только что сохраненный в БД

        /*проверяем,что полученные данные соответсвую ожидаемым*/
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("reviewId", 5)) //предудыщие 4 отзыва созданы в sql-скрипте
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("filmId", 5))
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("userId", 6))
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("isPositive", true))
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("content", "Отзыв от user6 на film5"));
    }

    /**
     * Проверка метода обновления информации об отзыве (updateReview)
     * Создаем и конфигурируем новый отзыв и подменяем им старый.
     * Проверяем, что в БД находится обновленная информация
     */
    @Test
    public void testUpdateReview() {
        /*создаем и конфигурируем отзыв*/
        Review newReview = new Review();
        newReview.setReviewId(1);
        newReview.setFilmId(1L);
        newReview.setUserId(1L);
        newReview.setIsPositive(false);
        newReview.setContent("ОБНОВЛЕННЫЙ Отзыв 1");

        int id = reviewDao.updateReview(newReview); //обновили отзыв в БД с помощью тестируемого метода
        Optional<Review> reviewOptional = Optional.of(reviewDao.getReviewById(id));//В optional помещаем отзыв, только что сохраненный в БД

        /*проверяем,что полученные данные соответсвую ожидаемым*/
        assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("reviewId", 1)) //предудыщие 4 отзыва созданы в sql-скрипте
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("filmId", 1))
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("userId", 1))
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("isPositive", false))
                .hasValueSatisfying(review ->
                        assertThat(review).hasFieldOrPropertyWithValue("content", "ОБНОВЛЕННЫЙ Отзыв 1"));
    }

    /**
     * Проверка метода получения всех отзывов по заданному фильму (getReviewsByFilm)
     */
    @Test
    public void testGetReviewsByFilm() {
        /*Получим отзывы по всем фильмам. Для этого передаем filmId = 0*/
        List<Review> reviewList = reviewDao.getReviewsByFilm(0, 10);

        assertThat(reviewList.size())
                .isEqualTo(4); //всего в БД 4 отзыва

        /*Получим отзывы по фильму с id=2. Для этого передаем filmId = 2*/
        reviewList = reviewDao.getReviewsByFilm(2, 10);

        assertThat(reviewList.size())
                .isEqualTo(1); //всего в БД 1 отзыв на фильм с id=2
    }

    /**
     * ПРоверка метода удаления отзыва по id (deleteReviewById)
     * Удаляем отзыв, а потом проверяем, что отзыв с таким id Отсутствует в БД
     */
    @Test
    public void testDeleteReviewById() {
        reviewDao.deleteReviewById(1);
        List<Review> reviewList = reviewDao.getReviewsByFilm(0, 10);

        /*список отзывов превращаем в стрим,фильтруем элементы по id=1 и смотрим, что ни
         * одного элемента не найдено (т.к. мы удалили отзыв с таким id)*/
        assertThat(reviewList.stream()
                .filter(review -> review.getReviewId() == 1)
                .findFirst())
                .isNotPresent();
    }

    /**
     * Проверка метода создания лайка на отзыв (createLike).
     * Ставим положительную оценку отзыву, проверяем, что она сохранилась в отзыве. Проверяем рейтинг отзыва.
     * Ставим Отрицательную оценку отзыву, проверяем, что она сохранилась в отзыве. Проверяем рейтинг отзыва.
     */
    @Test
    public void testCreateLike() {
        reviewDao.createLike(2, 1, 1); //создаем положительную оценку от пользователя id=1 На отзыв id=2

        Review review = reviewDao.getReviewById(2); //берем из базы отзыв с id = 2.
        Map<Integer, Integer> reviewLikes = review.getLikedUsers(); // получили оценки этого отзыва Map<userId, value>

        /*проверяем,что полученные данные соответсвую ожидаемым*/
        assertThat(reviewLikes.size())
                .isEqualTo(1); //в списке д.б. всего один отзыв
        assertThat(reviewLikes.get(1)) // проверяем, что пользователь с id=1
                .isEqualTo(1); //поставил положительную оценку +1
        assertThat(review.getUseful())
                .isEqualTo(1); //рейтинг отзыва должен стать 1

        reviewDao.createLike(2, 4, -1); //создаем отрицательную оценку от пользователя id=4 На отзыв id=2
        review = reviewDao.getReviewById(2); //берем из базы отзыв с id = 2.
        reviewLikes = review.getLikedUsers(); // получили оценки этого отзыва Map<userId, value>

        /*проверяем,что полученные данные соответсвую ожидаемым*/
        assertThat(reviewLikes.size())
                .isEqualTo(2); //в списке д.б. два отзыва
        assertThat(reviewLikes.get(4)) // проверяем, что пользователь с id=4
                .isEqualTo(-1); //поставил отрицательную оценку -1
        assertThat(review.getUseful())
                .isEqualTo(0);//рейтинг отзыва должен снова стать 0
    }

    /**
     * Проверяем метод удаления оценки у отзыва (deleteLike)
     * Добавляем несколько лайков, один из них удаляем. Смотрим, что удаленный отзыв отсуствует, а остальные остались.
     */
    @Test
    public void testDeleteLike() {
        /*создадим лайки на отзыв*/
        reviewDao.createLike(2, 1, 1);
        reviewDao.createLike(2, 2, 1);
        reviewDao.createLike(2, 3, -1);
        reviewDao.createLike(2, 4, -1);

        /*один из лайков удалим тестируемым методом.*/
        reviewDao.deleteLike(2, 3); //у отзыва с id=2 удалили лайк от пользователя с id=3

        Review review = reviewDao.getReviewById(2); //берем из базы отзыв с id = 2.
        Map<Integer, Integer> reviewLikes = review.getLikedUsers(); // получили оценки этого отзыва Map<userId, value>

        /*проверяем,что полученные данные соответсвую ожидаемым*/
        assertThat(reviewLikes.size())
                .isEqualTo(3); //в списке д.б. всего Три отзыва
        assertThat(reviewLikes.containsKey(1)) // проверяем, что остался лайк от пользователя id =1
                .isEqualTo(true);
        assertThat(reviewLikes.containsKey(2)) // проверяем, что остался лайк от пользователя id =2
                .isEqualTo(true);
        assertThat(reviewLikes.containsKey(3)) // проверяем, что не существует лайка от пользователя id =3
                .isEqualTo(false);
        assertThat(reviewLikes.containsKey(4)) // проверяем, что остался лайк от пользователя id =4
                .isEqualTo(true);
        assertThat(review.getUseful())
                .isEqualTo(1);//рейтинг отзыва должен стать 1 (+1 +1 -1 = 1)
    }
}
