package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Data
public class Film {

    private int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Duration duration;

    public Film(String name, String description, String releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = LocalDate.parse(releaseDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.duration = Duration.ofMinutes(duration);
    }
}
