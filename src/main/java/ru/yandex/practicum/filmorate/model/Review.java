package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Review { //отзывы на фильмы

    public Review() {
        this.likedUsers = new HashMap<>();
        this.userId = null;
        this.filmId = null;
        this.isPositive = null;
    }

    @JsonAlias("reviewId")
    int reviewId; //id Отзыва
    @NotNull
    Long filmId; //id фильма, к которому относится отзыв
    @NotNull
    Long userId; //id пользователя, оставившего отзыв
    @NotNull
    @Size(min = 1)
    String content; //текст отзыва
    int useful; //полезность отзыва. Значение основано на лайках этого отзыва
    @NotNull
    @JsonProperty("isPositive")
    Boolean isPositive; //положительный или отрицательный отзыв
    @JsonIgnore
    Map<Integer, Integer> likedUsers; //мап из id пользователей, лайкнувших отзыв, и их оценки: лайк +1, дизлайк -1

    /**
     * Преобразование объекта Review в HashMap
     * <название поля, поле>
     *
     * @return объект в виде мапы
     */
    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("film_id", filmId);
        values.put("user_id", userId);
        values.put("content", content);
        values.put("useful", useful);
        values.put("isPositive", isPositive);

        return values;
    }

    /**
     * Добавление пользователя к списку лайкнувших
     *
     * @param userId - id пользователя
     * @param like   - оценка
     */
    public void addUserLike(int userId, int like) {
        this.likedUsers.put(userId, like);
    }
}
