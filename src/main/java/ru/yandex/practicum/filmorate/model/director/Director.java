package ru.yandex.practicum.filmorate.model.director;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Director {
    private Long id;
    @NotEmpty
    @NotNull
    private String name;
}
