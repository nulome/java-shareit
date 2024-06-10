package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error("Error ValidationException 400 {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка данных", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleCustomValueException(final CustomValueException e) {
        log.error("Error CustomValueException 400 {}", e.getMessage());
        return new ErrorResponse(
                e.getMessage(), e.getLocalizedMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Error 400 {} \n {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse(
                e.getClass().getTypeName(), e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBookingUnavailableException(final BookingUnavailableException e) {
        log.error("Error 400 {} \n {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse(
                e.getClass().getTypeName(), e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.error("Error 404 {} \n {}", e.getClass().getSimpleName(), e.getMessage());

        return new ErrorResponse(
                e.getClass().getTypeName(), e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(final EntityNotFoundException e) {
        log.error("Error 404 {} \n {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse(
                e.getClass().getTypeName(), e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        log.error("Error 404 {} \n {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse(
                e.getClass().getTypeName(), e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEmptyResultDataAccessException(final EmptyResultDataAccessException e) {
        log.error("Error EmptyResultDataAccessException 404 {}", e.getMessage());
        return new ErrorResponse(
                "В базу данных передано неизвестное значение", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUnknownValueException(final UnknownValueException e) {
        log.error("Error UnknownValueException 404 {}", e.getMessage());
        return new ErrorResponse(
                "Неизвестное значение", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateKeyException(final DuplicateKeyException e) {
        log.error("Error DuplicateKeyException 409 {}", e.getMessage());
        return new ErrorResponse(
                "Конфликт значения", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleBadSqlGrammarException(final BadSqlGrammarException e) {
        log.error("Error BadSqlGrammarException 500 {}", e.getMessage());
        return new ErrorResponse(
                "Ошибка при запросе к базе данных.", e.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Error Throwable 500 {}", e.getMessage());
        return new ErrorResponse(
                "Произошла непредвиденная ошибка.", e.fillInStackTrace().toString()
        );
    }
}
