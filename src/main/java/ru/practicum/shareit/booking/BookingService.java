package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingResponse createBooking(int userId, CreateBookingRequestDto createBookingRequestDto);

    BookingResponse readBooking(int userId, int bookingId);

    BookingResponse changeBookingStatus(int userId, int bookingId, boolean approved);

    List<BookingResponse> getBookingsByUser(int userId, String state);

    List<BookingResponse> getBookingsByOwnerItem(int userId, String state);
}
