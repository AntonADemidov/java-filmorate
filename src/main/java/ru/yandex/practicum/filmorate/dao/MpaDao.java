package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDao {

    Mpa createMpa(int id);

    Collection<Mpa> getAllMpa();

    Mpa getMpaById(int id);
}