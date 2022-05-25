package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static final LocalDate REFERENCE_DATE = LocalDate.of(1895, 10, 28);
    private final Map<Integer, Film> films = new HashMap<>();
    private static int filmId = 0;

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        if (approveFilm(film)) {
            filmId++;
            film.setId(filmId);
            films.put(film.getId(), film);
            log.info("film added");
            return film;
        } else {
            throw new ValidationException("Film validation error");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (approveFilm(film) && films.containsKey(film.getId())) {
            if (films.containsKey(film.getId())) {
                films.replace(film.getId(), film);
                log.info("film updated");
                return film;
            }
        }
        throw new NotFoundException("Film not found");
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> result = new ArrayList<>(films.values());
        result.sort(new FilmComparator());
        if (films.size() > count) {
            return result.subList(films.size() - count, films.size());
        } else {
            return result;
        }
    }

    @Override
    public Film getFilmById(int id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException(String.format("Film with id: %s not found", id));
        }
    }

    @Override
    public String likeTheMovie(Integer id, Integer userId) {
        if (films.containsKey(id)) {
            return films.get(id).addUserLike(userId);
        }
        throw new NotFoundException(String.format("Film with id: %s not found", id));
    }

    @Override
    public String deleteTheMovieLike(Integer id, Integer userId) {
        if (films.containsKey(id)) {
            return films.get(id).deleteUserLike(userId);
        }
        throw new NotFoundException(String.format("Film with id: %s not found", id));
    }

    private boolean approveFilm(Film film) {
        if (!film.getName().isEmpty() && film.getDescription().length() > 0 && film.getDescription().length() <= 200 &&
                LocalDate.parse(film.getReleaseDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).isAfter(REFERENCE_DATE) &&
                film.getDuration() > 0) {
            if (films.values().stream().noneMatch(film1 -> film1.getName().equals(film.getName()))) {
                return true;
            }
            throw new NotFoundException("Not Found");
        }
        throw new ValidationException("Film validation error");
    }
}

class FilmComparator implements Comparator<Film>{
    @Override
    public int compare(Film o1, Film o2) {
        return o1.getRate() - o2.getRate();
    }
}