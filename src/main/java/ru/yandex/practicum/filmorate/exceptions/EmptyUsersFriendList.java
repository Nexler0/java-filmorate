package ru.yandex.practicum.filmorate.exceptions;

public class EmptyUsersFriendList extends RuntimeException {
    private final String message;

    public EmptyUsersFriendList(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
