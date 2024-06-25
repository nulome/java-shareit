package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.handler.BookingUnavailableException;
import ru.practicum.shareit.handler.CustomValueException;
import ru.practicum.shareit.handler.UnknownValueException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.StatusBooking.*;
import static ru.practicum.shareit.related.Constants.CONTROLLER_BOOKING_PATH;
import static ru.practicum.shareit.related.UtilityClasses.createPageRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;


    @Override
    public BookingResponse createBooking(int userId, CreateBookingRequestDto createBookingRequestDto) {
        log.info("Получен запрос Post {} - booker: {}", CONTROLLER_BOOKING_PATH, userId);
        validationDateStartAndEnd(createBookingRequestDto);
        int itemId = createBookingRequestDto.getItemId();
        User user = checkGetUserInDataBase(userId);
        Item item = checkItemInDB(itemId);
        checkAvailabilityBookingItem(item, userId, itemId);

        BookingDto bookingDto = bookingMapper.toBookingDto(createBookingRequestDto, user, item);

        Booking booking = bookingMapper.toBooking(bookingDto);
        booking = bookingRepository.save(booking);
        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse readBooking(int userId, int bookingId) {
        log.info("Получен запрос Get {}/{} от User {}", CONTROLLER_BOOKING_PATH, bookingId, userId);
        Booking booking = checkBookingInDB(bookingId);
        verificationAccessCreateUserOrBooker(userId, booking);

        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse changeBookingStatus(int userId, int bookingId, boolean approved) {
        log.info("Получен запрос PATCH {}/{}?approved={} from User {}", CONTROLLER_BOOKING_PATH, bookingId, approved, userId);
        Booking booking = checkBookingInDB(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new IllegalArgumentException("Не доступно изменение статуса Booking " + bookingId + " для User: " + userId);
        }
        updateStatusBooking(bookingId, approved, booking);
        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByUser(int userId, String state, Integer from, Integer size) {
        log.info("Получен запрос GET {}?state={} от User {}", CONTROLLER_BOOKING_PATH, state, userId);
        checkGetUserInDataBase(userId);
        Pageable pageable = createPageRequest(from, size);
        List<Booking> bookingList = getBookingsByState(userId, parseStringToState(state), pageable).getContent();
        return bookingList.stream()
                .map(bookingMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getBookingsByOwnerItem(int userId, String state, Integer from, Integer size) {
        log.info("Получен запрос GET {}/owner?state={} от OwnerItem {}", CONTROLLER_BOOKING_PATH, state, userId);
        checkGetUserInDataBase(userId);
        Pageable pageable = createPageRequest(from, size);
        List<Booking> bookingList = getBookingsByOwner(userId, parseStringToState(state), pageable).getContent();
        return bookingList.stream()
                .map(bookingMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    private void verificationAccessCreateUserOrBooker(int userId, Booking booking) {
        log.trace("Проверка доступа для Booking: {}", booking.getId());
        if (booking.getBooker().getId() != userId && booking.getItem().getOwner().getId() != userId) {
            throw new UnknownValueException("Запрет доступа пользователю: " + userId);
        }
    }

    private void validationDateStartAndEnd(CreateBookingRequestDto createBookingRequestDto) {
        log.trace("Проверка корректности периода бронирования к Item: {}", createBookingRequestDto.getItemId());
        if (!createBookingRequestDto.getStart().isBefore(createBookingRequestDto.getEnd()) ||
                createBookingRequestDto.getStart().equals(createBookingRequestDto.getEnd())) {
            throw new ValidationException("Дата End не может быть раньше или равна даты Start.");
        }
    }

    private void updateStatusBooking(int bookingId, boolean approved, Booking booking) {
        if (booking.getStatus() != WAITING) {
            throw new ValidationException("Статус WAITING уже был изменен.");
        }
        if (approved) {
            bookingRepository.updateStatusBooking(bookingId, APPROVED);
            booking.setStatus(APPROVED);
        } else {
            bookingRepository.updateStatusBooking(bookingId, REJECTED);
            booking.setStatus(REJECTED);
        }
    }

    private Page<Booking> getBookingsByState(int userId, StatusBooking state, Pageable pageable) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageable);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        dateTime, dateTime, pageable);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, dateTime, pageable);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, dateTime, pageable);
            default:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, state, pageable);
        }
    }

    private Page<Booking> getBookingsByOwner(int userId, StatusBooking state, Pageable pageable) {
        switch (state) {
            case ALL:
                return bookingRepository.getBookingsByOwnerItem(userId, pageable);
            case CURRENT:
                return bookingRepository.getBookingsByOwnerCurrent(userId, ZonedDateTime.now(), pageable);
            case PAST:
                return bookingRepository.getBookingsByOwnerPast(userId, ZonedDateTime.now(), pageable);
            case FUTURE:
                return bookingRepository.getBookingsByOwnerFuture(userId, ZonedDateTime.now(), pageable);
            default:
                return bookingRepository.getBookingsByOwnerItemAndStatus(userId, state, pageable);
        }
    }

    private User checkGetUserInDataBase(int userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе users не найдено: " + userId));
    }

    private StatusBooking parseStringToState(String state) {
        try {
            return StatusBooking.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new CustomValueException("Unknown state: " + state);
        }
    }

    private Item checkItemInDB(int itemId) {
        return itemRepository.getItemById(itemId).orElseThrow(() ->
                new EntityNotFoundException("Значение в базе не найдено для item: " + itemId));
    }

    private Booking checkBookingInDB(int bookingId) {
        return bookingRepository.getBookingById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Значение в базе не найдено для booking: " + bookingId));
    }

    private void checkAvailabilityBookingItem(Item item, int userId, int itemId) {
        if (item.getOwner().getId() == userId) {
            throw new IllegalArgumentException("Не доступно бронирование своего Item: " + itemId);
        }
        if (!item.getAvailable()) {
            throw new BookingUnavailableException("Не доступно бронирование запрошенного Item: " + itemId);
        }
    }
}