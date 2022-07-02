package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
