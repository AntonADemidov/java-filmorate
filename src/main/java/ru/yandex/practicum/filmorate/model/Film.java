package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    @NonFinal long id;
    String description;
    String name;
    LocalDate releaseDate;
    Integer duration;
    Mpa mpa;
    //Set<Long> userLikes = new HashSet<>();
    List<Genre> genres;
    List<Director> directors;


    public Film(long id, String description, String name, LocalDate releaseDate, Integer duration, Mpa mpa, List<Genre> genres) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = genres;
    }

    public Film(long id, String description, String name, LocalDate releaseDate, Integer duration, Mpa mpa) {
        this.id = id;
        this.description = description;
        this.name = name;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

//    //public long getLikes() {
//        return userLikes.size();
//    }
}