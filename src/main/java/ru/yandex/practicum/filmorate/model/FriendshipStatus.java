package ru.yandex.practicum.filmorate.model;

public enum FriendshipStatus {
    CONFIRMED("Подтвержденная"),
    UNCONFIRMED("Не подтвержденная");

    private final String status;

    FriendshipStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
