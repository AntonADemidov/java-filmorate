package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Service
public class DirectorService {
    private final DirectorDao directorDao;
    private long id = 0;

    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    private Director getDirectorOrThrowException(long id) {
        Director director = directorDao.getDirectorById(id);
        if (director == null) {
            throw new DataNotFoundException("Режиссер с указанным ID не найден");
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
