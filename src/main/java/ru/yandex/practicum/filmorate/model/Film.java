package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.director.Director;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.*;

@Data
@Validated
public class Film {
    private int id;
    @NotEmpty
    @NotBlank
    private final String name;
    @NotEmpty
    @NotBlank
    private final String description;
    @NotEmpty
    @NotBlank
    private final String releaseDate;
    private final int duration;
    private int rate;
    private List<Integer> likesId;
    private Mpa mpa;
    private List<Genre> genres;
    private List<Director> directors;


    public Film(String name, String releaseDate, String description, int duration, int rate) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        likesId = new ArrayList<>();
    }

    public String addUserLike(int userId) {
        if (userId > 0 && !likesId.contains(userId)) {
            likesId.add(userId);
            rate++;
            return String.format("Пользователь c id: %s, добавил лайк", id);
        } else {
            return "Лайк не добавлен";
        }
    }

    public void fillLikesList(int userId) {
        likesId.add(userId);
    }

    public String deleteUserLike(int userId) {
        if (likesId.contains(userId)) {
            rate--;
            likesId.removeIf(id -> id == userId);
            return String.format("Пользователь c id: %s, удалил лайк", id);
        }
        throw new NotFoundException("Пользователь не найден");
    }

    public void addGenre(Genre genre) {
        if (genres == null) {
            genres = new ArrayList<>();
            genres.add(genre);
        }
        if (!genres.stream().anyMatch(genre1 -> genre1.getId() == genre.getId())) {
            genres.add(genre);
        }
        genres.sort(new Comparator<Genre>() {
            @Override
            public int compare(Genre o1, Genre o2) {
                return o1.getId() - o2.getId();
            }
        });
    }

    public void createGenreStorage() {
        if (genres == null) {
            genres = new ArrayList<>();
        }
    }

    public void createDirectorsStorage() {
        if (directors == null) {
            directors = new ArrayList<>();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return duration == film.duration && Objects.equals(name, film.name)
                && Objects.equals(description, film.description) && Objects.equals(releaseDate, film.releaseDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, releaseDate, duration);
    }
}
