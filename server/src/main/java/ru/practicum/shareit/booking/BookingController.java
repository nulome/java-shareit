package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;

import javax.transaction.Transactional;
import java.util.List;

import static ru.practicum.shareit.related.Constants.CONTROLLER_BOOKING_PATH;
import static ru.practicum.shareit.related.Constants.REQUEST_HEADER_USER_KEY;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = CONTROLLER_BOOKING_PATH)
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse createBooking(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                         @RequestBody CreateBookingRequestDto createBookingRequestDto) {
        log.info("Получен запрос Post {} - booker: {}", CONTROLLER_BOOKING_PATH, userId);
        return bookingService.createBooking(userId, createBookingRequestDto);
    }

    @GetMapping("/{bookingId}")
    public BookingResponse readBooking(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int bookingId) {
        log.info("Получен запрос Get {}/{} от User {}", CONTROLLER_BOOKING_PATH, bookingId, userId);
        return bookingService.readBooking(userId, bookingId);
    }

    @Transactional
    @PatchMapping("/{bookingId}")
    public BookingResponse changeBookingStatus(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId, @PathVariable int bookingId,
                                               @RequestParam boolean approved) {
        log.info("Получен запрос Patch {}/{}?approved={} from User {}", CONTROLLER_BOOKING_PATH, bookingId, approved, userId);
        return bookingService.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingResponse> getBookingsByUser(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                   @RequestParam String state,
                                                   @RequestParam Integer from,
                                                   @RequestParam Integer size) {
        log.info("Получен запрос GET {}?state={} от User {}", CONTROLLER_BOOKING_PATH, state, userId);
        return bookingService.getBookingsByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponse> getBookingsByOwnerItem(@RequestHeader(REQUEST_HEADER_USER_KEY) int userId,
                                                        @RequestParam String state,
                                                        @RequestParam Integer from,
                                                        @RequestParam Integer size) {
        log.info("Получен запрос GET {}/owner?state={} от OwnerItem {}", CONTROLLER_BOOKING_PATH, state, userId);
        return bookingService.getBookingsByOwnerItem(userId, state, from, size);
    }

}



