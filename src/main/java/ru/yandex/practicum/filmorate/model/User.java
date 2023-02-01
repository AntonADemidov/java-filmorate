package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    private String name;
    private final String email;
    private final String login;
    private final LocalDate birthday;
}