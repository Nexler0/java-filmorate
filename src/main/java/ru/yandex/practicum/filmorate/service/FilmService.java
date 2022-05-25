package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;

    public FilmService(FilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public List<Film> findAllFilms() {
        return inMemoryFilmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(int count) {
        return inMemoryFilmStorage.getPopularFilms(count);
    }

    public Film getFilmById(int id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    public String likeTheMovie(Integer id, Integer userId) {
        return inMemoryFilmStorage.likeTheMovie(id, userId);
    }

    public String deleteTheMovieLike(Integer id, Integer userId) {
        return inMemoryFilmStorage.deleteTheMovieLike(id, userId);
    }
}
