package ru.yandex.practicum.filmorate.exceptions;

public class EmptyUsersFriendListException extends RuntimeException {
    private final String message;

    public EmptyUsersFriendListException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
