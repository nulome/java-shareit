package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;
import ru.practicum.shareit.related.CustomValueException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.related.Constants.CONTROLLER_BOOKING_PATH;
import static ru.practicum.shareit.related.Constants.REQUEST_HEADER_USER_KEY;


@Controller
@RequestMapping(path = CONTROLLER_BOOKING_PATH)
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                @RequestBody @Valid CreateBookingRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> readBooking(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                              @PathVariable Integer bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.readBooking(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> changeBookingStatus(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                      @PathVariable int bookingId,
                                                      @RequestParam(name = "approved") boolean approved) {
        log.info("Patch booking status {}, userId={}, approved={}", bookingId, userId, approved);
        return bookingClient.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUser(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = parseStateParam(stateParam);
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByUser(userId, state, from, size);
    }


    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = parseStateParam(stateParam);
        log.info("Get owner booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsByOwnerItem(userId, state, from, size);
    }

    private BookingState parseStateParam(String stateParam) {
        return BookingState.from(stateParam)
                .orElseThrow(() -> new CustomValueException("Unknown state: " + stateParam));
    }
}
