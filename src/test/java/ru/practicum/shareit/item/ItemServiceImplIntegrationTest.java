package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.StatusBooking;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.commentDto.CreateCommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemWithDateBookingResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@Transactional
class ItemServiceImplIntegrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemService itemService;

    private final EasyRandom random = new EasyRandom();

    @BeforeAll
    void setUp() {
        User user = new User(1, "nameUser1", "emailTestItemServ1@email.em");

        userRepository.save(user);

        Item item = Item.builder()
                .id(1)
                .available(true)
                .description("GroooverSpot")
                .name("name")
                .owner(user)
                .build();
        itemRepository.save(item);

        user = new User(2, "nameUser2", "TestItemServ12@email.em");
        userRepository.save(user);

        Booking booking = new Booking(1, ZonedDateTime.now().minusDays(1), ZonedDateTime.now().plusDays(1),
                item, user, StatusBooking.APPROVED);
        bookingRepository.save(booking);
        booking = new Booking(2, ZonedDateTime.now().minusDays(4), ZonedDateTime.now().minusDays(3),
                item, user, StatusBooking.APPROVED);
        bookingRepository.save(booking);
        Comment comment = new Comment(1, "TextComment", item, user, ZonedDateTime.now());
        commentRepository.save(comment);
    }

    @Test
    void getItems_whenRightRequest_thenActualListItemWithTextComment() {
        List<ItemWithDateBookingResponse> listActual = itemService.getItems(1, 0, 2);
        assertEquals(1, listActual.size());
        assertEquals(2, listActual.get(0).getLastBooking().getBookerId());
        assertEquals("TextComment", listActual.get(0).getComments().get(0).getText());
    }

    @Test
    void getItemsByTextSearch_whenRightRequest_thenActualListItemWithDescription() {
        List<ItemResponse> listActual = itemService.getItemsByTextSearch("versp", 0, 2);
        assertEquals(1, listActual.size());
        assertEquals("GroooverSpot", listActual.get(0).getDescription());
    }

    @Test
    void updateItem_whenRightRequest_thenActualUpdName() {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1, "UpdateName", "GroooverSpot", true);

        ItemResponse responseActual = itemService.updateItem(1, itemRequestDto);
        assertEquals(1, responseActual.getId());
        assertEquals("UpdateName", responseActual.getName());
    }

    @Test
    void createComment_whenRightRequest_thenActualComment() {
        CreateCommentRequestDto createCommentRequestDto = new CreateCommentRequestDto("TextCommentCreate2");
        CommentResponse commentActual = itemService.createComment(2, 1, createCommentRequestDto);

        assertEquals("nameUser2", commentActual.getAuthorName());
    }
}