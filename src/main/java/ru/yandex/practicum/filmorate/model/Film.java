package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Film {
    @NonFinal long id;
    String description;
    String name;
    LocalDate releaseDate;
    Integer duration;
    Set<Long> userLikes = new HashSet<>();
    public long likes() {
        return userLikes.size();
    }
}
