package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Mpa {
    Integer id;
    @NonFinal
    String name;

    public Mpa(Integer id) {
        this.id = id;
    }

    public Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}