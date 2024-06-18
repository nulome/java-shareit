package ru.practicum.shareit.request;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemRequestRepositoryTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private final EasyRandom random = new EasyRandom();
    private ItemRequest itemRequest;

    @BeforeAll
    void init() {
        User user1 = random.nextObject(User.class);
        user1.setId(1);
        userRepository.save(user1);
        itemRequest = new ItemRequest(1, "Item 1 By User 1 By Created Old", user1,
                ZonedDateTime.now().minusDays(8), null);
        itemRequestRepository.save(itemRequest);
        itemRequest = new ItemRequest(2, "Item 2 By User 1 By Created Eden", user1,
                ZonedDateTime.now().minusDays(4), null);
        itemRequestRepository.save(itemRequest);

        User user2 = random.nextObject(User.class);
        user2.setId(2);
        userRepository.save(user2);
        itemRequest = new ItemRequest(3, "Item 3 By User 2 By Created Eden", user2,
                ZonedDateTime.now().minusDays(3), null);
        itemRequestRepository.save(itemRequest);
        itemRequest = new ItemRequest(4, "Item 4 By User 2 By Created Old", user2,
                ZonedDateTime.now().minusDays(5), null);
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void getItemRequestById_whenRightRequest_thenRightResponse() {
        Optional<ItemRequest> itemActual = itemRequestRepository.getItemRequestById(4);

        assertTrue(itemActual.isPresent());
        assertEquals(4, itemActual.get().getId());
    }

    @Test
    void getItemRequestById_whenBadRequest_thenEmptyResponse() {
        Optional<ItemRequest> itemActual = itemRequestRepository.getItemRequestById(99);

        assertFalse(itemActual.isPresent());
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_whenRequestorOne_thenNotItemRequestorTwo() {
        List<ItemRequest> listActual = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1);

        assertEquals(2, listActual.size());
        assertEquals(1, listActual.get(0).getRequestor().getId());
        assertEquals(1, listActual.get(1).getRequestor().getId());
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc_whenRequestorTwo_thenOrderByCreated() {
        List<ItemRequest> listActual = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(2);

        assertEquals(3, listActual.get(0).getId());
        assertEquals(4, listActual.get(1).getId());
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_whenRequestorOne_thenNotItemRequestorOne() {
        List<ItemRequest> listActual = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(1,
                PageRequest.of(0, 10)).getContent();

        assertEquals(2, listActual.size());
        assertEquals(2, listActual.get(0).getRequestor().getId());
        assertEquals(2, listActual.get(1).getRequestor().getId());
    }

    @Test
    void findAllByRequestorIdNotOrderByCreatedDesc_whenRequestorTwo_thenOrderByCreate() {
        List<ItemRequest> listActual = itemRequestRepository.findAllByRequestorIdNotOrderByCreatedDesc(2,
                PageRequest.of(0, 10)).getContent();

        assertEquals(2, listActual.size());
        assertEquals(2, listActual.get(0).getId());
        assertEquals(1, listActual.get(1).getId());
    }


}