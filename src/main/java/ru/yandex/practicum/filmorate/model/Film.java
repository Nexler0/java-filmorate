package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

    public Film(String name, String description, String releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        likesId = new ArrayList<>();
    }

    public String addUserLike(int userId){
        rate++;
        likesId.add(userId);
        return String.format("Пользователь c id: %s, добавил лайк", id);
    }

    public String deleteUserLike(int userId){
        if (likesId.contains(userId)) {
            rate--;
            Iterator<Integer> likesListId = likesId.iterator();
            while (likesListId.hasNext()) {
                if (likesListId.next() == userId) {
                    likesListId.remove();
                    break;
                }
            }
            return String.format("Пользователь c id: %s, удалил лайк", id);
        }
        throw new NotFoundException("Пользователь не найден");
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
