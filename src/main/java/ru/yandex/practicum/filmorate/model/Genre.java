package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

@Data
public class Genre {
    private int id;
    private String name;

    public Genre() {
    }

    public Genre(int id) {
        this.id = id;

        switch (id) {
            case 1:
                name = "Комедия";
                break;
            case 2:
                name = "Драма";
                break;
            case 3:
                name = "Мультфильм";
                break;
            case 4:
                name = "Триллер";
                break;
            case 5:
                name = "Документальный";
                break;
            case 6:
                name = "Боевик";
                break;
            default:
                throw new NotFoundException("Такого Id нет");
        }
    }
}