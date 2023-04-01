package ru.yandex.practicum.filmorate.controller;

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
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/{id}")
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

    @DeleteMapping("/{id}")
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

    @PutMapping("/{id}/like/{userId}")
    public void createReviewLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавлен лайк от пользователя id={} на отзыв id = {}", userId, id);
        reviewService.addReviewLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void createReviewDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("Добавлен дизлайк от пользователя id={} на отзыв id = {}", userId, id);
        reviewService.addReviewDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteReviewLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удален лайк от пользователя id={} на отзыв id = {}", userId, id);
        reviewService.removeReviewLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void deleteReviewDislike(@PathVariable int id, @PathVariable int userId) {
        log.info("Удален дизлайк от пользователя id={} на отзыв id = {}", userId, id);
        reviewService.removeReviewLike(id, userId);
    }
}
