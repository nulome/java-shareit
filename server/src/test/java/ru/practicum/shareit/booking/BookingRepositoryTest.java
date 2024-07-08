package ru.practicum.shareit.booking;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private final EasyRandom random = new EasyRandom();
    private final int userId1 = 1;
    private final int userId2 = 2;

    private Booking booking;


    @BeforeAll
    void setUp() {
        User user = new User(1, "Names", "email@lol.com");
        userRepository.save(user);
        Item item = new Item(1, "Names", "Desc", true, user, null);
        itemRepository.save(item);

        booking = new Booking(1, ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                item, user, StatusBooking.APPROVED);
        bookingRepository.save(booking);
        booking = new Booking(2, ZonedDateTime.now().plusDays(7), ZonedDateTime.now().plusDays(9),
                item, user, StatusBooking.WAITING);
        bookingRepository.save(booking);
        booking = new Booking(3, ZonedDateTime.now().minusDays(4), ZonedDateTime.now().minusDays(3),
                item, user, StatusBooking.APPROVED);
        bookingRepository.save(booking);
        booking = new Booking(4, ZonedDateTime.now().minusDays(9), ZonedDateTime.now().minusDays(8),
                item, user, StatusBooking.APPROVED);
        bookingRepository.save(booking);
        booking = new Booking(5, ZonedDateTime.now().plusDays(4), ZonedDateTime.now().plusDays(8),
                item, user, StatusBooking.APPROVED);
        bookingRepository.save(booking);

        user = new User(2, "Names", "email222@lol.com");
        userRepository.save(user);
        item = new Item(2, "Names", "Desc2222", true, user, null);
        itemRepository.save(item);
        booking = new Booking(6, ZonedDateTime.now().plusDays(4), ZonedDateTime.now().plusDays(8),
                item, user, StatusBooking.APPROVED);
        bookingRepository.save(booking);
    }

    @Test
    void findAllByItemIdAndStatusOrderByStartDesc_whenRequestStatus_thenListSizeOne() {
        List<Booking> listActual = bookingRepository.findAllByItemIdAndStatusOrderByStartDesc(1, StatusBooking.WAITING);

        assertEquals(1, listActual.size());
        assertEquals("Desc", listActual.get(0).getItem().getDescription());
    }

    @Test
    void getBookingByBookerIdAndItemIdAndEndBeforeAndStatus_whenRightRequest_thenBookingByBooker() {
        Optional<Booking> bookingActual = bookingRepository.getBookingByBookerIdAndItemIdAndEndBeforeAndStatus(
                1, 1, ZonedDateTime.now(), "APPROVED");

        assertTrue(bookingActual.isPresent());
        assertEquals(3, bookingActual.get().getId());
    }

    @Test
    void updateStatusBooking_whenRequestStatus_thenBookingRejected() {
        bookingRepository.updateStatusBooking(2, StatusBooking.REJECTED);
        Optional<Booking> bookingActual = bookingRepository.getBookingById(2);

        assertTrue(bookingActual.isPresent());
        assertEquals(StatusBooking.REJECTED, bookingActual.get().getStatus());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByStartDesc_whenRequestStatus_thenAllByStatus() {
        List<Booking> listActual = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(1,
                StatusBooking.APPROVED, null).getContent();

        assertEquals(4, listActual.size());
        assertEquals(StatusBooking.APPROVED, listActual.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc_whenRequestCurrent_thenListSizeOne() {
        List<Booking> listActual = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                1, ZonedDateTime.now(), ZonedDateTime.now(), null).getContent();

        assertEquals(1, listActual.size());
        assertEquals(1, listActual.get(0).getId());
    }

    @Test
    void findAllByBookerIdAndStartAfterOrderByStartDesc_whenRequestFuture_thenListSizeTwo() {
        List<Booking> listActual = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(
                1, ZonedDateTime.now(), null).getContent();

        assertEquals(2, listActual.size());
        assertEquals(2, listActual.get(0).getId());
    }

    @Test
    void getBookingsByOwnerItem_whenRequestAll_thenListSizeOne() {
        List<Booking> listActual = bookingRepository.getBookingsByOwnerItem(2, null).getContent();

        assertEquals(1, listActual.size());
        assertEquals(6, listActual.get(0).getId());
    }

    @Test
    void getBookingsByOwnerItemAndStatus_whenRequestByStatus_thenListEmpty() {
        List<Booking> listActual = bookingRepository.getBookingsByOwnerItemAndStatus(
                2, StatusBooking.REJECTED, null).getContent();

        assertEquals(0, listActual.size());
    }

    @Test
    void getBookingsByOwnerPast_whenRequestByOwner_thenListSizeTwo() {
        List<Booking> listActual = bookingRepository.getBookingsByOwnerPast(
                1, ZonedDateTime.now(), null).getContent();

        assertEquals(2, listActual.size());
        assertEquals(3, listActual.get(0).getId());
    }
}