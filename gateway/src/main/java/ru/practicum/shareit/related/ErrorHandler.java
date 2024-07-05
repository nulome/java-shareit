package ru.practicum.shareit.related;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCustomValueException(final CustomValueException e) {
        log.error("Error CustomValueException 400 {}", e.getMessage());
        return new ErrorResponse(
                e.getMessage(), e.getLocalizedMessage()
        );
    }

}
