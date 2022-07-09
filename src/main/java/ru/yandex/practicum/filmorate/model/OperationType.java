package ru.yandex.practicum.filmorate.model;

public enum OperationType { REMOVE(1), ADD(2), UPDATE(3);
    private int id;

    OperationType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
