package ru.yandex.practicum.filmorate.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final Gson gson = new Gson();
    private Map<String, User> users = new HashMap<>();

    @GetMapping("/users")
    public Map<String, User> findAllUsers() {
        return users;
    }

    @PostMapping("/users")
    public String createUser(@Valid @RequestBody User user) {
        if (approveUser(user)) {
            users.put(user.getLogin(), user);
            log.info("User created {}", gson.toJson(user));
            return gson.toJson(user);
        } else {
            return null;
        }
    }

    @PutMapping("/users")
    public void updateFilm(@Valid @RequestBody User user) {
        if (approveUser(user)) {
            if (users.containsKey(user.getLogin())) {
                users.replace(user.getLogin(), user);
                log.info("User updated");
            } else {
                users.put(user.getLogin(), user);
                log.info("User created");
            }
        }
    }

    private boolean approveUser(User user) {
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getEmail().contains("@") && !user.getEmail().isEmpty() &&
                !user.getLogin().isEmpty() && !user.getLogin().isBlank() &&
                LocalDate.parse(user.getBirthday(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .isBefore(LocalDate.now())) {
            return true;
        } else {
            throw new ValidationException("User validation error");
        }
    }
}
