package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.time.Duration;
import java.time.LocalDate;

@Data
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Film {
    @NonFinal int id;
    String description;
    String name;
    LocalDate releaseDate;
    int duration;
}
