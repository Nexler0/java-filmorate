package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

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
    public Collection<Film> getRecommendations(int id) {
        return filmStorage.getRecommendations(id);
    }

    public List<Film> getSortByParamFilms(Integer directorId, String param) {
        return filmStorage.getSortByParamFilms(directorId, param);
    }

    //Возвращает список фильмов, отсортированных по популярности.
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        return filmStorage.getCommonFilms(userId, friendId);
    }

    public List<Film> getFilmsWithRequestedSearchParameters(String query, String by) {
        return filmStorage.getFilmsWithRequestedSearchParameters(query, getSetOfSearchParams(by));
    }

    //Проверка параметров поиска
    private Set<FilmSearchParam> getSetOfSearchParams(String by) {
        Set<FilmSearchParam> searchParams = new HashSet<>();
        String[] params;
        if (by.contains(",")) {
            params = by.split(",");
        } else {
            params = new String[]{by};
        }
        for (String param : params) {
            for (FilmSearchParam filmSearchParam : FilmSearchParam.values()) {
                if (param.equalsIgnoreCase(filmSearchParam.name())) {
                    searchParams.add(filmSearchParam);
                }
            }
        }
        if (searchParams.size() > 0 && searchParams.size() == params.length) {
            return searchParams;
        } else {
            throw new ValidationException("Заданные параметры недоступны для поиска!");
        }
    }

    public List<Film> getPopularFilmsByYear(Integer count, Integer year){
        return filmStorage.getPopularFilmsByYear(count, year);
    }

    public List<Film> getPopularFilmsByGenre(Integer count, Integer genreId){
        if (genreId < 1 || genreId > 6){
            throw new NotFoundException("Такого жанра не существует!");
        }else {
            return filmStorage.getPopularFilmsByGenre(count, genreId);
        }
    }
    public List<Film> getPopularFilmsByGenreAndYear(Integer count, Integer genreId, Integer year){
        if (genreId < 1 || genreId > 6){
            throw new NotFoundException("Такого жанра не существует!");
        }else {
            return filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);
        }

    }
}
