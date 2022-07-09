package ru.yandex.practicum.filmorate.model;

public enum EventType { LIKE(1), REVIEW(2), FRIEND(3);
    private int id;

    EventType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
