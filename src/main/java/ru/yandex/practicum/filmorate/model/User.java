package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class User {
    private int id;
    private final String email;
    private final String login;
    private String name;
    private String birthday;

    public User(String email, String login, String name, String birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }
}