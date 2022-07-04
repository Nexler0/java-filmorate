package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.director.Director;
import ru.yandex.practicum.filmorate.model.director.FilmDirector;
import ru.yandex.practicum.filmorate.storage.DirectorDao;
import ru.yandex.practicum.filmorate.storage.FilmDirectorsDao;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorDao directorDao;
    private final FilmDirectorsDao filmDirectorsDao;

    public DirectorController(DirectorDao directorDao, FilmDirectorsDao filmDirectorsDao) {
        this.directorDao = directorDao;
        this.filmDirectorsDao = filmDirectorsDao;
    }

    /**
     * Запрос на создание нового директора, добавление его в таблицу
     * Post add new director and add him to DB
     * @param director
     * @return
     */
    @PostMapping
    public Director create(@RequestBody Director director){
        log.info("Получен запрос к эндпоинту /directors. Метод POST");
        if (director.getName().isBlank() || director.getName() == null) {
            throw new ValidationException("Необходимо указать имя директора!");
        } else {
            directorDao.addDirector(director);
            return director;
        }

    }

    /**
     * Запрос на получение директора из таблицы по его ID
     * Get directors by ID from DB
     *
     * @param directorId
     * @return
     */
    @GetMapping("/{id}")
    public Optional<Director> directorById(@PathVariable("id") Integer directorId) {
        if (directorDao.containsById(directorId)) {
            return directorDao.getDirById(directorId);
        } else {
            throw new NotFoundException("Нет такого директора c ID " + directorId);
        }
    }

    /**
     * Запрос на обновление директора
     * Update old director
     *
     * @param director
     * @return
     */
    @PutMapping
    public Optional<Director> update(@RequestBody Director director) {
        log.info("Получен запрос к эндпоинту /users. Метод PUT");
        if (director.getId() > 0) {
            if (directorDao.containsById(director.getId())){
                return directorDao.updateDirectors(director);
            } else {
                throw new NotFoundException("Такого директора не существует" + director.getId());
            }
        } else {
            throw new NotFoundException("Id директора отрицательный - " + director.getId());
        }
    }

    /**
     * Запрос на получение списка всех директоров
     * Get all directors from DB
     *
     * @return
     */
    @GetMapping
    public List<Director> allDirectors() {
        return directorDao.getAllDirector();
    }

    /**
     * Запрос на удаление директора по его ID из DB
     * Delete Directors from DB
     * @param directorId
     * @return
     */
    @DeleteMapping("/{id}")
    public String deleteDirector(@PathVariable("id") Integer directorId) {
        //Удаление связности из таблиц, сначала удалили из сопоставления, потом удалили из директора
        List<FilmDirector> listFilmOfDirector = filmDirectorsDao.findFilmByDirector(directorId);
        for (FilmDirector filmDirector : listFilmOfDirector) {
            filmDirectorsDao.deleteDirectorFromFilm(filmDirector.getFilmId(), directorId);
        }
        if (directorDao.containsById(directorId)){
            directorDao.deleteDirector(directorId);
            return "Директор удален с Id = " + directorId;
        }else {
            throw new NotFoundException("Директор отсутсвует в базе - " + directorId);
        }

    }

}
