package ru.yandex.practicum.filmorate.exceptions;

public class EmptyReviewsListException extends RuntimeException {
    private final String message;

    public EmptyReviewsListException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
