package ru.practicum.shareit.handler;

public class BookingUnavailableException extends RuntimeException {
    public BookingUnavailableException(String message) {
        super(message);
    }

    public BookingUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
