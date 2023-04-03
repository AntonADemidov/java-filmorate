package ru.yandex.practicum.filmorate.exception;

public class DataAlreadyExistException extends Exception {
    public DataAlreadyExistException(String message) {
        super(message);
    }
}