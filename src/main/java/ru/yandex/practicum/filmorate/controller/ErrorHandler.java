package ru.yandex.practicum.filmorate.controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.EmptyFilmsListException;
import ru.yandex.practicum.filmorate.exceptions.EmptyUsersFriendListException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse incorrectParameter(final ValidationException e){
        return new ErrorResponse("Error", String.format("Ошибка “%s”", e.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse incorrectParameter(final NotFoundException e){
        return new ErrorResponse("Error", String.format("Ошибка “%s”", e.getMessage()));
    }

    @ExceptionHandler({EmptyUsersFriendListException.class, EmptyFilmsListException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse incorrectParameter(final RuntimeException e){
        return new ErrorResponse("Error", String.format("Ошибка “%s”", e.getMessage()));
    }
}

class ErrorResponse {
    private final String error;
    private final String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }
}
