package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {

    @GetMapping
    public List<Genre> getAllGenres(){
        List<Genre> rates = new ArrayList<>();
        for (int i = 1; i <= 6; i++){
            rates.add(new Genre(i));
        }
        return rates;
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id){
        return new Genre(id);
    }
}
