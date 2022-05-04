package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;

@Data
public class Film {
    private int id;
    private final String name;
    private final String description;
    private final String releaseDate;
    private final Duration duration;

    public Film(String name, String description, String releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = Duration.ofMinutes(duration);
    }
}
