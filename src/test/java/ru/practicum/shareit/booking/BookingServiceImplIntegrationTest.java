package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingResponse;
import ru.practicum.shareit.booking.dto.CreateBookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class BookingServiceImplIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private BookingService bookingService;

    private final EasyRandom random = new EasyRandom();

    @BeforeAll
    void setUp() {
        User user = new User(1, "name", "email1@email.em");
        userRepository.save(user);

        Item item = Item.builder()
                .id(1)
                .available(true)
                .description("desc")
                .name("name")
                .owner(user)
                .build();
        itemRepository.save(item);

        user.setId(2);
        user.setEmail("email2@email.em");
        userRepository.save(user);

        Booking booking = new Booking(1, ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                item, user, StatusBooking.APPROVED);
        bookingRepository.save(booking);

        item.setId(2);
        item.setOwner(user);
        itemRepository.save(item);
    }

    @Test
    void createBooking_whenRightRequest_thenRightBookingWithStatus() {
        CreateBookingRequestDto createBookingRequestDto = new CreateBookingRequestDto(LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), 2);
        bookingService.createBooking(1, createBookingRequestDto);

        BookingResponse bookingResponse = bookingService.readBooking(1, 2);
        assertEquals(2, bookingResponse.getId());
        assertEquals(StatusBooking.WAITING, bookingResponse.getStatus());
        assertEquals(1, bookingResponse.getBooker().getId());
    }

    @Test
    void getBookingsByOwnerItem_whenRightRequest_thenBookingCurrentOne() {
        List<BookingResponse> listActual = bookingService.getBookingsByOwnerItem(1, "CURRENT",
                null, null);

        assertFalse(listActual.isEmpty());
        assertEquals(1, listActual.size());
        assertEquals(1, listActual.get(0).getId());
        assertEquals(2, listActual.get(0).getBooker().getId());
    }

}