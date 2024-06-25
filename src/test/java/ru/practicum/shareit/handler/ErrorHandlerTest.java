package ru.practicum.shareit.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    ErrorHandler errorHandler;

    @BeforeEach
    void setUp() {
        errorHandler = new ErrorHandler();
    }

    @Test
    void handleValidationException() {
        ErrorResponse handleActual = errorHandler.handleValidationException(new ValidationException("Test"));
        assertEquals("Test", handleActual.getDescription());
    }

    @Test
    void handleCustomValueException() {
        ErrorResponse handleActual = errorHandler.handleCustomValueException(new CustomValueException("Test"));
        assertEquals("Test", handleActual.getDescription());
    }

    @Test
    void handleBookingUnavailableException() {
        ErrorResponse handleActual = errorHandler.handleBookingUnavailableException(new BookingUnavailableException("Test"));
        assertEquals("Test", handleActual.getDescription());
    }

    @Test
    void handleEntityNotFoundException() {
        ErrorResponse handleActual = errorHandler.handleEntityNotFoundException(new EntityNotFoundException("Test"));
        assertEquals("Test", handleActual.getDescription());
    }

    @Test
    void handleIllegalArgumentException() {
        ErrorResponse handleActual = errorHandler.handleIllegalArgumentException(new IllegalArgumentException("Test"));
        assertEquals("Test", handleActual.getDescription());
    }

    @Test
    void handleUnknownValueException() {
        ErrorResponse handleActual = errorHandler.handleUnknownValueException(new UnknownValueException("Test"));
        assertEquals("Test", handleActual.getDescription());
    }

    @Test
    void handleDuplicateKeyException() {
        ErrorResponse handleActual = errorHandler.handleDuplicateKeyException(new DuplicateKeyException("Test"));
        assertEquals("Test", handleActual.getDescription());
    }

    @Test
    void handleThrowable() {
        ErrorResponse handleActual = errorHandler.handleThrowable(new Throwable("Test"));
        assertEquals("java.lang.Throwable: Test", handleActual.getDescription());
    }

}