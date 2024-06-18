package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.model.Booking;

import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {

    private BookingMapper bookingMapper;

    @BeforeEach
    void setUp() {
        bookingMapper = new BookingMapperImpl();
    }

    @Test
    void toBookingDto() {
        BookingDto booking = bookingMapper.toBookingDto(null, null, null);
        assertNull(booking);
    }

    @Test
    void toBookingResponse() {
        BookingResponse booking = bookingMapper.toBookingResponse(null);
        assertNull(booking);
    }

    @Test
    void toBooking() {
        Booking booking = bookingMapper.toBooking(null);
        assertNull(booking);
    }

}