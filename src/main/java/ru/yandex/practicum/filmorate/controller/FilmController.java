package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmService.getFilmById(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(name = "count", defaultValue = "10") Integer count,
                                      @RequestParam(name = "genreId", required = false) Integer genreId,
                                      @RequestParam(name = "year", required = false) Integer year) {
        if (genreId == null && year == null) {
            return filmService.getPopularFilms(count);
        } else if (genreId == null) {
            return filmService.getPopularFilmsByYear(count, year);
        } else if (year == null) {
            return filmService.getPopularFilmsByGenre(count, genreId);
        } else {
            return filmService.getPopularFilmsByGenreAndYear(count, genreId, year);
        }
    }

    @GetMapping("/search")
    public List<Film> getFilmsWithRequestedSearchParameters(@RequestParam String query, @RequestParam String by) {
        return filmService.getFilmsWithRequestedSearchParameters(query, by);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public String likeTheMovie(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.likeTheMovie(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String deleteTheMovieLike(@PathVariable Integer id, @PathVariable Integer userId) {
        return filmService.deleteTheMovieLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public String deleteTheMovie(@PathVariable int id) {
        return filmService.deleteTheMovie(id);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getSortByParamFilms(@PathVariable Integer directorId,
                                          @RequestParam(name = "sortBy",
                                                  required = false) String param) {
        return filmService.getSortByParamFilms(directorId, param);
    }

    //???????????????????? ???????????? ??????????????, ?????????????????????????????? ???? ????????????????????????.
    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam("userId") int userId
            , @RequestParam("friendId") int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }


}
