package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends RuntimeException{
    private final String message;

    public ValidationException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
