package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmDbStorage) {
        this.filmStorage = filmDbStorage;
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilmById(id);
    }

    public String likeTheMovie(Integer id, Integer userId) {
        return filmStorage.likeTheMovie(id, userId);
    }

    public String deleteTheMovieLike(Integer id, Integer userId) {
        return filmStorage.deleteTheMovieLike(id, userId);
    }

    public String deleteTheMovie(int id) {
        return filmStorage.deleteTheMovie(id);
    }

    //Получить список рекомендованных фильмов для пользователя по id
    public Collection<Film> getRecommendations(int id){
        return filmStorage.getRecommendations(id);
    }

    public List<Film> getSortByParamFilms(Integer directorId, String param){
        return filmStorage.getSortByParamFilms(directorId, param);
    }
}
