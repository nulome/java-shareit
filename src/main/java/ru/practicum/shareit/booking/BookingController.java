package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@RequestHeader("X-Sharer-User-Id") int userId,
                                         @RequestBody @Valid CreateBookingRequestDto createBookingRequestDto) {
        return bookingService.createBooking(userId, createBookingRequestDto);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse readBooking(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId) {
        return bookingService.readBooking(userId, bookingId);
    }

    @Transactional
    @PatchMapping("/{bookingId}")
    public BookingResponse changeBookingStatus(@RequestHeader("X-Sharer-User-Id") int userId, @PathVariable int bookingId,
                                               @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingResponse> getBookingsByUser(@RequestHeader("X-Sharer-User-Id") int userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(required = false) Integer from,
                                                   @RequestParam(required = false) Integer size) {
        return bookingService.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsByOwnerItem(@RequestHeader("X-Sharer-User-Id") int userId,
                                                        @RequestParam(defaultValue = "ALL") String state,
                                                        @RequestParam(required = false) Integer from,
                                                        @RequestParam(required = false) Integer size) {
        return bookingService.getBookingsByOwnerItem(userId, state, from, size);
    }

}
