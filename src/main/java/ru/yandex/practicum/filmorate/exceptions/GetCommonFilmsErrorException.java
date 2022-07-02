package ru.yandex.practicum.filmorate.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCommonFilmsErrorException extends RuntimeException {
    private final String error;
    private final String description;

    public GetCommonFilmsErrorException(String message, String error, String description) {
        super(message);
        this.error = error;
        this.description = description;
    }
}
