package ru.yandex.practicum.filmorate.controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.*;

@RestControllerAdvice
@Slf4j
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

    @ExceptionHandler({GetRecommendedFilmsErrorException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse GetRecommendedFilmsErrorExceptionHandler (final GetRecommendedFilmsErrorException exception){
        log.warn("Error: {}, Description: {}, message: {}", exception.getError(), exception.getDescription(),
                exception.getMessage());
        return new ErrorResponse(exception.getError(), exception.getDescription());
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
