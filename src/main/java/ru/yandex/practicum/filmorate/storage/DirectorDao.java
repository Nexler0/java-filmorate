package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.director.Director;

import java.util.List;
import java.util.Optional;

/**
 * Для работы с БД Directors
 * For DB Directors
 */
public interface DirectorDao {
    /**
     * Метод для добавления директора в ДБ
     * Add directors in to DB
     *
     * @param director
     * @return
     */
    Optional<Director> addDirector(Director director);

    /**
     * Метод для обновления директора в ДБ
     * Update director in to DB
     *
     * @param director
     * @return
     */
    Optional<Director> updateDirectors(Director director);

    /**
     * Метод для удаления директора из БД
     * Delete directors from DB
     *
     * @param id
     */
    void deleteDirector(Integer id);

    /**
     * Получение директора по его ID из БД
     * Get director by ID from Db
     *
     * @param id
     * @return
     */
    Optional<Director> getDirById(Integer id);

    /**
     * Метод получения всех директоров из БД
     * Get all directors from DB
     *
     * @return
     */
    List<Director> getAllDirector();

    /**
     * Проверка наличия директора в ДБ
     * Check contains directors in to DB
     *
     * @param id
     * @return
     */
    boolean containsById(Integer id);
}
