package ru.yandex.practicum.filmorate.exceptions;

public class EmptyFilmsListException extends RuntimeException {
    private final String message;

    public EmptyFilmsListException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
