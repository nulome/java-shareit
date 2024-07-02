package ru.practicum.shareit.request;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.CreateItemRequestReqDto;
import ru.practicum.shareit.request.dto.ItemRequestResponse;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private UserRepository userRepository;

    private final EasyRandom random = new EasyRandom();

    CreateItemRequestReqDto createItemRequestReqDto;

    @BeforeAll
    void setUp() {
        User user = random.nextObject(User.class);
        user.setId(1);
        userRepository.save(user);
        user.setId(2);
        user.setEmail("email@2.ru");
        userRepository.save(user);

        createItemRequestReqDto = random.nextObject(CreateItemRequestReqDto.class);

    }

    @Test
    void createdRequest_whenRightRequest_thenActualItemRequest() {
        createItemRequestReqDto.setDescription("TestCreatedService");
        ItemRequestResponse itemRequestActual = itemRequestService.createdRequest(1, createItemRequestReqDto);

        assertEquals("TestCreatedService", itemRequestActual.getDescription());
        assertEquals(1, itemRequestActual.getRequestor().getId());
        assertNotNull(itemRequestActual.getCreated());
    }

    @Test
    void createdRequest_whenBedRequestIdUser_thenThrows() {
        assertThrows(EntityNotFoundException.class, () -> {
            itemRequestService.createdRequest(99, createItemRequestReqDto);
        });
    }

    @Test
    @Transactional
    void getRequestsAll_whenRightRequest_thenListSizeOne() {
        itemRequestService.createdRequest(1, createItemRequestReqDto);
        itemRequestService.createdRequest(1, createItemRequestReqDto);

        itemRequestService.createdRequest(2, createItemRequestReqDto);

        List<ItemRequestResponse> listActual = itemRequestService.getRequestsAll(1, 0, 5);

        assertNotNull(listActual);
        assertEquals(1, listActual.size());
        assertEquals(2, listActual.get(0).getRequestor().getId());
    }

}