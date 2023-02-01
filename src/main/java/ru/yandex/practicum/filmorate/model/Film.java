package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    private final String description;
    private final String name;
    private final LocalDate releaseDate;
    private final int duration;
}
