package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmSearchParam;

import java.util.List;
import java.util.Set;

public interface FilmStorage {

    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getPopularFilms(int count);

    Film getFilmById(int id);

    String likeTheMovie(Integer id, Integer userId);

    String deleteTheMovieLike(Integer id, Integer userId);

    String deleteTheMovie(int id);

    List<Film> getFilmsWithRequestedSearchParameters(String query, Set<FilmSearchParam> searchParams);
}

