package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequestMapping("/directors")
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DirectorController {
    DirectorService directorService;
    private static final String actionWithId = "/{id}";

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) throws ValidationException {
        return directorService.createDirector(director);
    }

    @GetMapping
    public Collection<Director> getAllDirectors() {
        return directorService.getAllDirectors();
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) throws ValidationException {
        return directorService.updateDirector(director);
    }

    @GetMapping(actionWithId)
    public Director getDirectorById(@PathVariable long id) {
        return directorService.getDirectorById(id);
    }

    @DeleteMapping(actionWithId)
    public void deleteDirector(@PathVariable long id) {
        directorService.deleteDirector(id);
    }
}
