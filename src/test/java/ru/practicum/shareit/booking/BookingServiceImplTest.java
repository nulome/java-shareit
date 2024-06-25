package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingMapper bookingMapper;

    BookingService bookingService;
    private final EasyRandom random = new EasyRandom();
    private final int userId = 1;
    private User user;
    private Item item;
    BookingDto bookingDto;
    Booking booking;
    BookingResponse bookingResponse;

    @BeforeEach
    void setUp() {
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository, bookingMapper);
        user = random.nextObject(User.class);
        user.setId(userId);
        item = random.nextObject(Item.class);
        item.setId(1);
        User user2 = random.nextObject(User.class);
        user2.setId(2);
        item.setOwner(user2);
        bookingDto = random.nextObject(BookingDto.class);
        booking = random.nextObject(Booking.class);
        bookingResponse = random.nextObject(BookingResponse.class);
    }

    @Test
    void createBooking_whenRightRequest_thenVerifyMethod() {
        CreateBookingRequestDto createBookingRequestDto = random.nextObject(CreateBookingRequestDto.class);
        createBookingRequestDto.setStart(LocalDateTime.now().minusHours(1));
        createBookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));

        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.getItemById(createBookingRequestDto.getItemId())).thenReturn(Optional.of(item));
        when(bookingMapper.toBookingDto(any(CreateBookingRequestDto.class), any(User.class), any(Item.class)))
                .thenReturn(bookingDto);
        when(bookingMapper.toBooking(any(BookingDto.class))).thenReturn(booking);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        bookingService.createBooking(userId, createBookingRequestDto);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_whenBadRequestOwnerUser_thenThrows() {
        CreateBookingRequestDto createBookingRequestDto = random.nextObject(CreateBookingRequestDto.class);
        createBookingRequestDto.setStart(LocalDateTime.now().minusHours(1));
        createBookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));
        item.getOwner().setId(1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.getItemById(createBookingRequestDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(userId, createBookingRequestDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenBadRequestItemEmptyDB_thenThrows() {
        CreateBookingRequestDto createBookingRequestDto = random.nextObject(CreateBookingRequestDto.class);
        createBookingRequestDto.setStart(LocalDateTime.now().minusHours(1));
        createBookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));
        item.getOwner().setId(1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.getItemById(createBookingRequestDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.createBooking(userId, createBookingRequestDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenBadRequestAvailable_thenThrows() {
        CreateBookingRequestDto createBookingRequestDto = random.nextObject(CreateBookingRequestDto.class);
        createBookingRequestDto.setStart(LocalDateTime.now().minusHours(1));
        createBookingRequestDto.setEnd(LocalDateTime.now().plusDays(1));
        item.getOwner().setId(2);
        item.setAvailable(false);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.getItemById(createBookingRequestDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(BookingUnavailableException.class, () -> bookingService.createBooking(userId, createBookingRequestDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBooking_whenBadRequestValidRequest_thenThrows() {
        CreateBookingRequestDto createBookingRequestDto = random.nextObject(CreateBookingRequestDto.class);
        createBookingRequestDto.setStart(LocalDateTime.now().minusHours(1));
        createBookingRequestDto.setEnd(LocalDateTime.now().minusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.createBooking(userId, createBookingRequestDto));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void readBooking_whenRightRequest_thenVerifyMethod() {
        booking.getBooker().setId(userId);
        when(bookingRepository.getBookingById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        bookingService.readBooking(userId, 1);
        verify(bookingRepository).getBookingById(anyInt());
    }

    @Test
    void readBooking_whenBadRequestNotCreateUserOrBooker_thenThrows() {
        booking.getBooker().setId(userId);
        booking.getItem().getOwner().setId(2);
        when(bookingRepository.getBookingById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(UnknownValueException.class, () -> bookingService.readBooking(3, 1));
        verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }

    @Test
    void changeBookingStatus_whenRightRequestRejected_thenVerifyMethod() {
        booking.getItem().getOwner().setId(userId);
        booking.setStatus(StatusBooking.WAITING);
        when(bookingRepository.getBookingById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        bookingService.changeBookingStatus(userId, 1, false);
        assertEquals(StatusBooking.REJECTED, booking.getStatus());
        verify(bookingRepository).getBookingById(anyInt());
    }

    @Test
    void changeBookingStatus_whenRightRequestApprove_thenVerifyMethod() {
        booking.getItem().getOwner().setId(userId);
        booking.setStatus(StatusBooking.WAITING);
        when(bookingRepository.getBookingById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        bookingService.changeBookingStatus(userId, 1, true);
        assertEquals(StatusBooking.APPROVED, booking.getStatus());
        verify(bookingRepository).getBookingById(anyInt());
    }

    @Test
    void changeBookingStatus_whenBadRequestNotOwner_thenThrows() {
        booking.getItem().getOwner().setId(userId);
        when(bookingRepository.getBookingById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(IllegalArgumentException.class, () -> bookingService.changeBookingStatus(2, 1, false));
        verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }

    @Test
    void changeBookingStatus_whenBadRequestStatus_thenThrows() {
        booking.getItem().getOwner().setId(userId);
        booking.setStatus(StatusBooking.REJECTED);
        when(bookingRepository.getBookingById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.changeBookingStatus(userId, 1, false));
        verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }

    @Test
    void getBookingsByUser_whenRightRequestCurrent_thenVerifyMethod() {
        Page<Booking> page = new PageImpl<>(new ArrayList<>(List.of(booking)), PageRequest.of(0, 2), 1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyInt(),
                any(ZonedDateTime.class), any(ZonedDateTime.class), any(Pageable.class))).thenReturn(page);
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        bookingService.getBookingsByUser(userId, "CURRENT", 0, 2);
        verify(bookingRepository).findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(anyInt(),
                any(ZonedDateTime.class), any(ZonedDateTime.class), any(Pageable.class));
    }

    @Test
    void getBookingsByUser_whenRightRequestPast_thenVerifyMethod() {
        Page<Booking> page = new PageImpl<>(new ArrayList<>(List.of(booking)), PageRequest.of(0, 2), 1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(anyInt(),
                any(ZonedDateTime.class), any(Pageable.class))).thenReturn(page);
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        bookingService.getBookingsByUser(userId, "PAST", 0, 2);
        verify(bookingRepository).findAllByBookerIdAndEndBeforeOrderByStartDesc(anyInt(),
                any(ZonedDateTime.class), any(Pageable.class));
    }

    @Test
    void getBookingsByUser_whenRightRequestApprove_thenVerifyMethod() {
        Page<Booking> page = new PageImpl<>(new ArrayList<>(List.of(booking)), PageRequest.of(0, 2), 1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyInt(),
                any(StatusBooking.class), any(Pageable.class))).thenReturn(page);
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        bookingService.getBookingsByUser(userId, "APPROVED", 0, 2);
        verify(bookingRepository).findAllByBookerIdAndStatusOrderByStartDesc(anyInt(),
                any(StatusBooking.class), any(Pageable.class));
    }

    @Test
    void getBookingsByUser_whenBadRequestNotStatus_thenThrows() {
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        assertThrows(CustomValueException.class, () -> bookingService.getBookingsByUser(userId, "BadStatus", 1, 2));
        verify(bookingMapper, never()).toBookingResponse(any(Booking.class));
    }

    @Test
    void getBookingsByOwnerItem_whenRightRequestFuture_thenVerifyMethod() {
        Page<Booking> page = new PageImpl<>(new ArrayList<>(List.of(booking)), PageRequest.of(0, 2), 1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.getBookingsByOwnerFuture(anyInt(), any(ZonedDateTime.class), any(Pageable.class)))
                .thenReturn(page);
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        bookingService.getBookingsByOwnerItem(userId, "FUTURE", 0, 2);
        verify(bookingRepository).getBookingsByOwnerFuture(anyInt(), any(ZonedDateTime.class), any(Pageable.class));
    }

    @Test
    void getBookingsByOwnerItem_whenRightRequestApprove_thenVerifyMethod() {
        Page<Booking> page = new PageImpl<>(new ArrayList<>(List.of(booking)), PageRequest.of(0, 2), 1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.getBookingsByOwnerItemAndStatus(anyInt(), any(StatusBooking.class), any(Pageable.class)))
                .thenReturn(page);
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        bookingService.getBookingsByOwnerItem(userId, "APPROVED", 0, 2);
        verify(bookingRepository).getBookingsByOwnerItemAndStatus(anyInt(), any(StatusBooking.class), any(Pageable.class));
    }

    @Test
    void getBookingsByOwnerItem_whenRightRequestAll_thenVerifyMethod() {
        Page<Booking> page = new PageImpl<>(new ArrayList<>(List.of(booking)), PageRequest.of(0, 2), 1);
        when(userRepository.getUserById(userId)).thenReturn(Optional.of(user));
        when(bookingRepository.getBookingsByOwnerItem(anyInt(), any(Pageable.class)))
                .thenReturn(page);
        when(bookingMapper.toBookingResponse(any(Booking.class))).thenReturn(bookingResponse);

        bookingService.getBookingsByOwnerItem(userId, "ALL", 0, 2);
        verify(bookingRepository).getBookingsByOwnerItem(anyInt(), any(Pageable.class));
    }
}