package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Service
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DirectorService {
    DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    private Director getDirectorOrThrowException(long id) {
        Director director = directorDao.getDirectorById(id);
        if (director == null) {
            throw new DataNotFoundException(String.format("Режиссер с id #%d не найден", id));
        } else
            return director;
    }

    public Director createDirector(Director director) throws ValidationException {
        directorDao.createDirector(director);
        return directorDao.getLastAddedDirector();
    }

    public Collection<Director> getAllDirectors() {
        return directorDao.getAllDirectors();
    }

    public Director updateDirector(Director director) throws ValidationException {
        getDirectorOrThrowException(director.getId());
        directorDao.updateDirector(director);
        return director;
    }

    public Director getDirectorById(long id) {
        return getDirectorOrThrowException(id);
    }

    public void deleteDirector(long id) {
        getDirectorOrThrowException(id);
        directorDao.deleteDirectorById(id);
    }
}