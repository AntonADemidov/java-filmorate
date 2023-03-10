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
public class User {
    @NonFinal long id;
    @NonFinal String name;
    String email;
    String login;
    LocalDate birthday;
    Set<Long> friendIds = new HashSet<>();
    Set<Long> filmLikes = new HashSet<>();
}