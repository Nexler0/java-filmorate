package ru.yandex.practicum.filmorate.model.director;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FilmDirector {
    private Integer filmId;
    private Integer directorsId;
}
