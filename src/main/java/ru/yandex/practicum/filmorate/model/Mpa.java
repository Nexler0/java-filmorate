package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

@Data
public class Mpa {
    private int id;
    private String name;

    public Mpa() {
    }

    public Mpa(int id) {
        this.id = id;
        switch (id){
            case 1:
                name = "G";
                break;
            case 2:
                name = "PG";
                break;
            case 3:
                name = "PG-13";
                break;
            case 4:
                name = "R";
                break;
            case 5:
                name = "NC-17";
                break;
            default:
                throw new NotFoundException("Такого ID нет");
        }
    }
}
