package ru.yandex.practicum.filmorate.service;

public enum FilmSearchParam {
    DIRECTOR("directors.name"),
    TITLE("films.name");

    private final String name;

    FilmSearchParam(String name) {
        this.name = name;
    }

    public static String getName(FilmSearchParam filmSearchParam) {
        return filmSearchParam.name;
    }
}
