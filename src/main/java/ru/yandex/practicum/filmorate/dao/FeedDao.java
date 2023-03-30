package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.exception.DataAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FeedDao {

    List<Feed> getFeeds(long id);

    void addFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}