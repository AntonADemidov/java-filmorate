package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReviewController {
    ReviewService reviewService;
    private static final String actionWithId = "/{id}";
    private static final String actionWithLike = "/{id}/like/{userId}";
    private static final String actionWithDislike = "/{id}/dislike/{userId}";

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping(actionWithId)
    public Review getReview(@PathVariable int id) {
        Review review = reviewService.getById(id);
        log.info("Получен отзыв по id={}", review.getReviewId());
        return review;
    }

    @PostMapping
    public Review postReview(@Valid @RequestBody Review newReview) {
        Review review = reviewService.createReview(newReview);
        log.info("Создан отзыв. Id = {}", review.getReviewId());
        return reviewService.getById(review.getReviewId());
    }

    @DeleteMapping(actionWithId)
    public void deleteReview(@PathVariable int id) {
        reviewService.deleteReviewById(id);
        log.info("Удален Отзыв. Id = {}", id);
    }

    @PutMapping
    public Review putReview(@Valid @RequestBody Review updatedReview) {
        Review review = reviewService.update(updatedReview);
        log.info("Обновлен отзыв. Id = {}", review.getReviewId());
        return reviewService.getById(review.getReviewId());
    }

    @GetMapping()
    public List<Review> getAllReviews(@RequestParam(defaultValue = "0") int filmId, @RequestParam(defaultValue = "10") int count) {
        log.info("Выведены отзывы на фильм id = {}, в количестве {}", filmId, count);
        return reviewService.getAllReviews(filmId, count);
    }

    @PutMapping(actionWithLike)
    public void createReviewLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавлен лайк от пользователя id={} на отзыв id = {}", userId, id);
        reviewService.addReviewLike(id, userId);
    }

    @PutMapping(actionWithDislike)
    public void createReviewDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавлен дизлайк от пользователя id={} на отзыв id = {}", userId, id);
        reviewService.addReviewDislike(id, userId);
    }

    @DeleteMapping(actionWithLike)
    public void deleteReviewLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удален лайк от пользователя id={} на отзыв id = {}", userId, id);
        reviewService.removeReviewLike(id, userId);
    }

    @DeleteMapping(actionWithDislike)
    public void deleteReviewDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удален дизлайк от пользователя id={} на отзыв id = {}", userId, id);
        reviewService.removeReviewLike(id, userId);
    }
}
