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
    void toBookingDto_whenResponseNull_thenResultNull() {
        BookingDto booking = bookingMapper.toBookingDto(null, null, null);
        assertNull(booking);
    }

    @Test
    void toBookingResponse_whenResponseNull_thenResultNull() {
        BookingResponse booking = bookingMapper.toBookingResponse(null);
        assertNull(booking);
    }

    @Test
    void toBooking_whenResponseNull_thenResultNull() {
        Booking booking = bookingMapper.toBooking(null);
        assertNull(booking);
    }

}