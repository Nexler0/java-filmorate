package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {
    private static final LocalDate REFERENCE_DATE = LocalDate.of(1895, 10, 28);
    private Map<String, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Map<String, Film> findAllFilm() {
        return films;
    }

    @PostMapping("/films")
    public void addFilm(@Valid @RequestBody Film film) {
        if (approveFilm(film)) {
            films.put(film.getName(), film);
            log.info("film added");
        }
    }

    @PutMapping("/films")
    public void updateFilm(@Valid @RequestBody Film film) {
        if (approveFilm(film)) {
            if (films.containsKey(film.getName())) {
                films.replace(film.getName(), film);
                log.info("film updated");
            } else {
                films.put(film.getName(), film);
                log.info("film added");
            }
        }
    }

    private boolean approveFilm(Film film) {
        if (!film.getName().isEmpty() &&
                film.getDescription().length() > 0 &&
                film.getDescription().length() <= 200 &&
                LocalDate.parse(film.getReleaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        .isAfter(REFERENCE_DATE) &&
                !film.getDuration().isNegative()) {
            return true;
        } else {
            throw new ValidationException("Film validation error");
        }
    }
}
