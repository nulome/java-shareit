package ru.practicum.shareit.related;


public class CustomValueException extends RuntimeException {
    public CustomValueException(String message) {
        super(message);
    }

    public CustomValueException(final String message, final Throwable e) {
        super(message, e);
    }
}
