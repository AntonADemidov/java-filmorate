package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Genre {
    @NonFinal Integer id;
    @NonFinal String name;

    public Genre(Integer id) {
        this.id = id;
    }

    public Genre(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}