package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("Получен запрос Post /bookings - booker: {}", userId);
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
        log.info("Получен запрос Get /bookings/{} от User {}", bookingId, userId);
        Booking booking = checkBookingInDB(bookingId);
        verificationAccessCreateUserOrBooker(userId, booking);

        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public BookingResponse changeBookingStatus(int userId, int bookingId, boolean approved) {
        log.info("Получен запрос PATCH /bookings/{}?approved={} from User {}", bookingId, approved, userId);
        Booking booking = checkBookingInDB(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new IllegalArgumentException("Не доступно изменение статуса Booking " + bookingId + " для User: " + userId);
        }
        updateStatusBooking(bookingId, approved, booking);
        return bookingMapper.toBookingResponse(booking);
    }

    @Override
    public List<BookingResponse> getBookingsByUser(int userId, String state) {
        log.info("Получен запрос GET /bookings?state={} от User {}", state, userId);
        checkGetUserInDataBase(userId);
        List<Booking> bookingList = getBookingsByState(userId, parseStringToState(state));
        return bookingList.stream()
                .map(bookingMapper::toBookingResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getBookingsByOwnerItem(int userId, String state) {
        log.info("Получен запрос GET /bookings/owner?state={} от OwnerItem {}", state, userId);
        checkGetUserInDataBase(userId);
        List<Booking> bookingList = getBookingsByOwner(userId, parseStringToState(state));
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

    private List<Booking> getBookingsByState(int userId, StatusBooking state) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        switch (state) {
            case ALL:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, dateTime, dateTime);
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, dateTime);
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, dateTime);
            default:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, state);
        }
    }

    private List<Booking> getBookingsByOwner(int userId, StatusBooking state) {
        switch (state) {
            case ALL:
                return bookingRepository.getBookingsByOwnerItem(userId);
            case CURRENT:
                return bookingRepository.getBookingsByOwnerCurrent(userId, ZonedDateTime.now());
            case PAST:
                return bookingRepository.getBookingsByOwnerPast(userId, ZonedDateTime.now());
            case FUTURE:
                return bookingRepository.getBookingsByOwnerFuture(userId, ZonedDateTime.now());
            default:
                return bookingRepository.getBookingsByOwnerItemAndStatus(userId, state);
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