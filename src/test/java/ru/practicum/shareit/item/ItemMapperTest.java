package ru.practicum.shareit.item;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.commentDto.CommentResponse;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.InfoBookingByItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    private ItemMapper itemMapper;
    private final EasyRandom random = new EasyRandom();

    @BeforeEach
    void setUp() {
        itemMapper = new ItemMapperImpl();
    }

    @Test
    void toItemResponse() {
        ItemRequest itemRequest = new ItemRequest(1, "text", null, null, null);
        Item item = new Item(1, "text", "text", true, null, itemRequest, null);

        ItemResponse itemResponseActual = itemMapper.toItemResponse(item);
        assertEquals(1, itemResponseActual.getId());
        assertEquals(1, itemResponseActual.getRequestId());
    }

    @Test
    void toItemDto() {
        Item item = random.nextObject(Item.class);
        ItemDto actual = itemMapper.toItemDto(item);
        assertEquals(item.getOwner().getId(), actual.getOwner().getId());
    }

    @Test
    void toItemDto_whenCreateDto() {
        CreateItemRequestDto create = random.nextObject(CreateItemRequestDto.class);
        User user = random.nextObject(User.class);
        ItemRequest itemRequest = random.nextObject(ItemRequest.class);

        ItemDto actual = itemMapper.toItemDto(create, user, itemRequest);
        assertEquals(create.getName(), actual.getName());
    }

    @Test
    void toItemDto_whenNull() {
        ItemDto actual = itemMapper.toItemDto(null, null, null);
        assertNull(actual);
    }

    @Test
    void toItemResponse_when1() {
        ItemResponse actual = itemMapper.toItemResponse(null);
        assertNull(actual);
    }

    @Test
    void toComment_when1() {
        Comment actual = itemMapper.toComment(null);
        assertNull(actual);
    }

    @Test
    void toCommentResponse_when1() {
        CommentResponse actual = itemMapper.toCommentResponse(null);
        assertNull(actual);
    }

    @Test
    void toItem() {
        Item item = itemMapper.toItem(null);
        assertNull(item);
    }

    @Test
    void toInfoBookingByItemDto() {
        InfoBookingByItemDto item = itemMapper.toInfoBookingByItemDto(null);
        assertNull(item);
    }

}