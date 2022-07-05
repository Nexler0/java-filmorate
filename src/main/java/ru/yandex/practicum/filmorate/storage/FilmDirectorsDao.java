package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.director.FilmDirector;

import java.util.List;

/**
 * Для работы с БД FILMS_DIRECTORS
 * For DB FILMS_DIRECTORS
 */
public interface FilmDirectorsDao {
    /**
     * Метод для добавления директора к фильму
     * Add directors to film in to DB
     */
    void addDirectorToFilm(Integer filmId, Integer directorId);

    /**
     * Удалить директора у фильма из DB
     * Delete director from film in DB
     */
    void deleteDirectorFromFilm(Integer filmId, Integer directorId);

    /**
     * Проверка наличия директора у фильма
     * Contains director in Film in DB
     */
    boolean containsDirectorInFilmById(Integer filmId, Integer directorId);

    /**
     * Найти директоров по ид фильма в БД
     * Find directors Film by ID in DB
     */
    List<FilmDirector> findDirectorByFilms(Integer filmId);

    /**
     * Найти все фильмы по ID диретора
     * Find all films by id of directors
     */
    List<FilmDirector> findFilmByDirector(Integer directorId);
}
