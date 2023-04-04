package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedDao {

    List<Feed> getFeeds(long id);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    void addReview(long userId, long reviewId);

    void removeReview(long userId, long reviewId);

    void updateReview(long userId, long reviewId);
}