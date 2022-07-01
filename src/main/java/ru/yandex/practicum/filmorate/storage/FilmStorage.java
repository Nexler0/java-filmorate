package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getPopularFilms(int count);

    Film getFilmById(int id);

    String likeTheMovie(Integer id, Integer userId);

    String deleteTheMovieLike(Integer id, Integer userId);

    String deleteTheMovie(int id);

    /**
     * Метод возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска
     * The method returns a list of the director's films sorted by number of likes or year of release.
     * @param directorId
     * @param param
     * @return
     */
    List<Film> getSortByParamFilms(Integer directorId, String param);
}
