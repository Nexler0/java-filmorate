package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getPopularFilms(int count);

    Film getFilmById(int id);

    String likeTheMovie(Integer id, Integer userId);

    String deleteTheMovieLike(Integer id, Integer userId);

    String deleteTheMovie(int id);

    Collection<Film> getRecommendations(int id);
}
