package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {

    private static final int MPA_ID_START = 1;
    private static final int MPA_ID_FINISH = 5;

    @GetMapping
    public List<Mpa> getAllMpa() {
        List<Mpa> rates = new ArrayList<>();
        for (int i = MPA_ID_START; i <= MPA_ID_FINISH; i++) {
            rates.add(new Mpa(i));
        }
        return rates;
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        return new Mpa(id);
    }
}
