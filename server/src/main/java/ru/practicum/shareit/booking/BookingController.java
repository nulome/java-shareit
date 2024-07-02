package ru.practicum.shareit.booking;

import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;

import java.util.List;

import static ru.practicum.shareit.related.Constants.CONTROLLER_BOOKING_PATH;
import static ru.practicum.shareit.related.Constants.REQUEST_HEADER_USER_KEY;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = CONTROLLER_BOOKING_PATH)
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                         @RequestBody CreateBookingRequestDto createBookingRequestDto) {
        return bookingService.createBooking(userId, createBookingRequestDto);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse readBooking(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int bookingId) {
        return bookingService.readBooking(userId, bookingId);
    }

    @Transactional
    @PatchMapping("/{bookingId}")
    public BookingResponse changeBookingStatus(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int bookingId,
                                               @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingResponse> getBookingsByUser(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                   @RequestParam String state,
                                                   @RequestParam Integer from,
                                                   @RequestParam Integer size) {
        return bookingService.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsByOwnerItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                        @RequestParam String state,
                                                        @RequestParam Integer from,
                                                        @RequestParam Integer size) {
        return bookingService.getBookingsByOwnerItem(userId, state, from, size);
    }

}
