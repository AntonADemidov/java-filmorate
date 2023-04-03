package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashMap;
import java.util.Map;

@Data
public class Review { //отзывы на фильмы

    public Review() {
        this.likedUsers = new HashMap<>();
        this.userId = null;
        this.filmId = null;
        this.isPositive = null;
    }

    @JsonAlias("reviewId")
    private int reviewId; //id Отзыва
    @NotNull
    private Long filmId; //id фильма, к которому относится отзыв
    @NotNull
    private Long userId; //id пользователя, оставившего отзыв
    @NotNull
    @Size(min = 1)
    private String content; //текст отзыва
    private int useful; //полезность отзыва. Значение основано на лайках этого отзыва
    @NotNull
    @JsonProperty("isPositive")
    private Boolean isPositive; //положительный или отрицательный отзыв
    @JsonIgnore
    private Map<Integer, Integer> likedUsers; //мап из id пользователей, лайкнувших отзыв, и их оценки: лайк +1, дизлайк -1

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
