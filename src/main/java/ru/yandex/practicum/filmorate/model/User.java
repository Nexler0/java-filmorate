package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import javax.validation.constraints.*;

import java.util.*;

@Data
@Validated
public class User {
    private int id;
    @Email
    @NotEmpty
    @NotBlank
    private final String email;
    private String name;
    @NotBlank
    @NotEmpty
    private final String login;
    @NotEmpty
    @NotBlank
    private String birthday;
    private List<Integer> friendsList = new ArrayList<>();

    public User(String email, String login, String name, String birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public String getName() {
        if (name.isEmpty()) {
            return login;
        } else {
            return name;
        }
    }

    public String addFriend(int id) {
        friendsList.add(id);
        return String.format("Пользователь c id: %s, добавлен в список друзей", id);
    }

    public String deleteFriend(int id) {
        if (friendsList.contains(id)) {
            friendsList.removeIf(friend -> friend == id);
            return String.format("Пользователь c id: %s, удален из списка друзей", id);
        }
        throw new NotFoundException("Пользователь не найден");
    }

    public List<Integer> getAllFriends() {
        return new ArrayList<>(friendsList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(login, user.login)
                && Objects.equals(name, user.name) && Objects.equals(birthday, user.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, login, name, birthday, friendsList);
    }
}